import java.util.ArrayList;
import java.util.HashMap;

public class Router {
    private int routerId; // router Id
    private DVTable dvtable; //Distance Vector Table
    private ArrayList<Router> adjacentRouter; // Adjacent Routers
    private boolean isConverge;
    private boolean cti;
    public enum ROUTING_METHOD {BASIC, SPLIT_HORIZON, POISON_REVERSE}
    /**
     * Constructor
     * @param id
     */
    public Router(int id) {
        isConverge = false;
        cti = false;
        routerId = id;
        dvtable = new DVTable();
        adjacentRouter = new ArrayList<>();
    }

    /**
     * Check if the Distance Vector is converged
     * @return
     */
    public boolean IsConverge(){
        return isConverge;
    }
    public boolean IsCTI(){
        return cti;
    }
    /**
     * Get Router ID
     * @return
     */
    public int GetId(){
        return routerId;
    }

    /**
     * Advertise Distance Vector to all adjacent Routers
     */
    public void Advertise(ROUTING_METHOD method) {
        HashMap<Integer,DVCell> dvector;
        if(isConverge) {
            return;
        }
        if(method == ROUTING_METHOD.BASIC) {
            for (Router r : adjacentRouter) {
                r.AddDVector(routerId, dvtable.GetDVector(routerId));
            }
        } else {
            dvector = dvtable.GetDVector(routerId);
            for (Router r : adjacentRouter) {
                HashMap<Integer, DVCell> newdvector = new HashMap<>();
                for (Integer i : dvector.keySet()) {
                    newdvector.put(i, new DVCell(dvector.get(i)));
                    if (dvector.get(i).GetNextHop() == r.GetId()) {
                        newdvector.get(i).SetDV(method == ROUTING_METHOD.SPLIT_HORIZON ? -2 : -1);
                    }
                }
                r.AddDVector(routerId, newdvector);
            }
        }
    }

    /**
     * Add/Update Distance Vector
     * (It does not check if the the new DV is from an Adjacent Router)
     * @param id
     * @param dvector
     */
    public void AddDVector(int id, HashMap<Integer,DVCell> dvector) {
        dvtable.AddDVector(id,dvector);
        isConverge = false;
    }
    /**
     * Add an adjacent router
     * @param r
     * @param cost
     */
    public void AddAdjacentRouter(Router r, int cost) {
        adjacentRouter.add(r);
        dvtable.SetCell(routerId,r.GetId(),new DVCell(r.GetId(),1,cost,cost));
    }

    public int GetLinkCost(int id) {
        return dvtable.GetCell(routerId,id).GetCost();
    }
    public void SetLinkCost(int id, int cost) {
        if(cost < 0) {
            dvtable.GetCell(routerId,id).SetNextHop(0);
            dvtable.GetCell(routerId,id).SetHops(0);
        }
        dvtable.GetCell(routerId,id).SetDV(cost);
        isConverge = false;
    }
    public void RemoveAdjacentRouter(Router r) {
        if(adjacentRouter.contains(r)) {
            adjacentRouter.remove(r);
            r.SetLinkCost(routerId, -1);
            SetLinkCost(r.GetId(), -1);
        }
    }
    public DVCell GetCell(int id) {
        return dvtable.GetCell(routerId,id);
    }
    public void PrintDVTable(){
        System.out.println("DVTable of Router " + routerId + " :");
        dvtable.PrintTable(true);
    }
    public void PrintDVector() {
        dvtable.PrintDVector(routerId,true);
    }
    /**
     * Update the DV
     */
    public void UpdateDateDVector(){
        //If the DV table is converged, no need to update
        if(isConverge) {
            return;
        }
        HashMap<Integer,DVCell> dvector = dvtable.GetDVector(routerId);
        boolean isUpdate = false;
        //Find the least cost path to any router in the network
        for(Integer i : dvector.keySet()) {
            if(i != routerId) {
                int min = dvector.get(i).GetCost();
                Router nextHop = this;
                int dvcost;
                for(Router r : adjacentRouter) {
                    //Calculate the path if the cost is not < 0
                    if(dvector.get(r.GetId()).GetCost() >= 0 &&
                            dvtable.GetCell(r.GetId(),i).GetDV() >= 0) {
                        dvcost = dvector.get(r.GetId()).GetCost() + dvtable.GetCell(r.GetId(), i).GetDV();
                    } else {
                        dvcost = -1; // otherwise, the the selected path is < 0 (unreachable)
                    }
                    if(min < 0 || (min >= dvcost && dvcost > 0)) {
                        min = dvcost;
                        nextHop = r;
                    }
                }
                // If the dv cost is different, update it
                if(dvector.get(i).GetDV() != min) {
                    dvector.get(i).SetDV(min);
                    dvector.get(i).SetHops((nextHop != this) ? nextHop.GetCell(i).GetHops() + 1 : 0);
                    if(dvector.get(i).GetHops() > 100) {
                        cti = true;
                    }
                    isUpdate = true;
                }
            }
        }
        //If nothing change then the DV table converges
        if(!isUpdate) {
            isConverge = true;
        }
    }

    /**
     * Tracing the path to get the correct Distance
     * @param dest
     * @return
     */
    public Integer TracingPath(int dest) {
        int hops = 0;
        int id = routerId;
        int nid;
        //Find the hops by tracing the path
        while(id != dest) {
            hops++;
            nid = dvtable.GetCell(id,dest).GetNextHop();
            if(nid != dest && id == dvtable.GetCell(nid,dest).GetNextHop()) {
                hops = -1;
                break;
            }
            id = nid;
        }
        return hops;
    }
}
