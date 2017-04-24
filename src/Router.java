import java.util.ArrayList;
import java.util.HashMap;

public class Router {
    private int routerId; // router Id
    private DVTable dvtable; //Distance Vector Table
    private ArrayList<Router> adjacentRouter; // Adjacent Routers
    private boolean isConverge;
    private boolean cti;
    public enum METHOD {BASIC, SPLIT_HORIZON, POISON_REVERSE}
    private METHOD method;
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
        method = METHOD.BASIC;
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
    public void Advertise(METHOD method, boolean force) {
        this.method = method;
        HashMap<Integer,DVCell> dvector;
        if(force) {
            isConverge = false;
        } else if(isConverge) {
            return;
        }
        if(method == METHOD.BASIC) {
            for (Router r : adjacentRouter) {
                r.AddDVector(routerId, dvtable.GetDVector(routerId));
            }
        } else {
            dvector = dvtable.GetDVector(routerId);
            for(Router r : adjacentRouter) {
                HashMap<Integer, DVCell> newdvector = new HashMap<>();
                for (Integer i : dvector.keySet()) {
                    newdvector.put(i, new DVCell(dvector.get(i)));
                    if (dvector.get(i).GetNextHop() == r.GetId()) {
                        newdvector.get(i).SetDV((method == METHOD.SPLIT_HORIZON)? -2 : -1);
                    }
                }
                r.AddDVector(routerId,newdvector);
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

    /**
     * Get the link cost
     * @param id
     * @return
     */
    public int GetLinkCost(int id) {
        return dvtable.GetCell(routerId,id).GetCost();
    }

    /**
     * Set the link cost
     * @param id
     * @param cost
     */
    public void SetLinkCost(int id, int cost) {
        if(cost < 0) {
            dvtable.GetCell(routerId,id).SetNextHop(-1);
            dvtable.GetCell(routerId,id).SetHops(-1);
            dvtable.GetCell(routerId,id).SetDV((method == METHOD.SPLIT_HORIZON)? -2 : -1);
        }
        dvtable.GetCell(routerId,id).SetCost(cost);
        isConverge = false;
    }

    /**
     * Remove a neighbor router
     * @param r
     */
    public void RemoveAdjacentRouter(Router r) {
        if(adjacentRouter.contains(r)) {
            adjacentRouter.remove(r);
            SetLinkCost(r.GetId(), (method == METHOD.SPLIT_HORIZON) ? -2 : -1);
        }
    }

    /**
     * Get a cell from the distance table
     * @param id
     * @return
     */
    public DVCell GetCell(int id) {
        return dvtable.GetCell(routerId,id);
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
                    //Ignore if the cost is infinity or NaN
                    if(dvector.get(r.GetId()).GetCost() >= 0 &&
                            dvtable.GetCell(r.GetId(),i).GetDV() >= 0) {
                        dvcost = dvector.get(r.GetId()).GetCost() + dvtable.GetCell(r.GetId(), i).GetDV();
                    } else {
                        dvcost = (method == METHOD.SPLIT_HORIZON) ? -2 : -1;
                    }
                    if(min < 0 || (min >= dvcost && dvcost > 0)) {
                        min = dvcost;
                        nextHop = r;
                    }
                                    }
                // If the dv cost is different, update it
                if(dvector.get(i).GetDV() != min) {
                    dvector.get(i).SetDV(min);
                    if(dvector.get(i).GetHops() > 100) {
                        cti = true;
                    }
                    if(nextHop != this) {
                        dvector.get(i).SetNextHop(nextHop.GetId());
                        dvector.get(i).SetHops(nextHop.GetCell(i).GetHops() + 1);
                    } else if (nextHop == this) {
                        dvector.get(i).SetNextHop(-1);
                        dvector.get(i).SetHops(-1);
                    }
                    isUpdate = true;
                }
            }
        }
        //If nothing change then the DV table is converged
        if(!isUpdate) {
            isConverge = true;
        }
    }

    /**
     * Print current router's Distance Table
     */
    public void PrintTable(){
        dvtable.PrintTable();
    }

    /**
     * Print the current router's Distance Vector
     */
    public void PrintDVector() {
        dvtable.PrintDVector(routerId);
    }
}
