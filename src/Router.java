import java.util.ArrayList;
import java.util.HashMap;

public class Router {
    private int routerId; // router Id
    private DVTable dvtable; //Distance Vector Table
    private ArrayList<Router> adjacentRouter; // Adjacent Routers
    private boolean isConverge;

    /**
     * Constructor
     * @param id
     */
    public Router(int id) {
        isConverge = false;
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
    public void Advertise() {
        if(!isConverge) {
            for (Router r : adjacentRouter) {
                r.AddDVector(routerId, dvtable.GetDVector(routerId));
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
        if(cost == -1) {
            dvtable.GetCell(routerId,id).SetNextHop(0);
            dvtable.GetCell(routerId,id).SetHops(0);
            dvtable.GetCell(routerId,id).SetDV(-1);
        }
        dvtable.GetCell(routerId,id).SetCost(cost);
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
    public void PrintDVector(){
        System.out.println("DVector of Router " + routerId + " :");
        HashMap<Integer,DVCell> dvector = dvtable.GetDVector(routerId);
        for(Integer i : dvector.keySet()) {
            System.out.print("   |   " + i + "           ");
        }
        System.out.println();
        for(Integer i : dvector.keySet()) {
            System.out.print("   | Cost: ");
            if(dvector.get(i).GetCost() < 0) {
                System.out.print("INFINITY");
            } else {
                System.out.print(dvector.get(i).GetCost());
                System.out.print("       ");
            }
        }
        System.out.println();
        for(Integer i : dvtable.GetDVector(routerId).keySet()) {
            System.out.print("   | DV: ");
            if(dvector.get(i).GetDV() < 0) {
                System.out.print("INFINITY");
            } else {
                System.out.print(dvector.get(i).GetDV());
                System.out.print("       ");
            }
        }
        System.out.println();
        for(Integer i : dvtable.GetDVector(routerId).keySet()) {
            System.out.print("   | NextHop: " + dvtable.GetCell(routerId,i).GetNextHop());
            System.out.print("    ");
        }
        System.out.println();
        for(Integer i : dvtable.GetDVector(routerId).keySet()) {
            System.out.print("   | Hops: "+ dvtable.GetCell(routerId,i).GetHops());
            System.out.print("       ");
        }
        System.out.println();
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
                    if(dvector.get(r.GetId()).GetCost() >= 0 &&
                            dvtable.GetCell(r.GetId(),i).GetDV() >= 0) {
                        dvcost = dvector.get(r.GetId()).GetCost() + dvtable.GetCell(r.GetId(), i).GetDV();
                    } else {
                        dvcost = -1;
                    }
                    if(min < 0 || (min >= dvcost && dvcost > 0)) {
                        min = dvcost;
                        nextHop = r;
                    }
                                    }
                // If the dv cost is different, update it
                if(dvector.get(i).GetDV() != min) {
                    dvector.get(i).SetDV(min);
                    if(nextHop != this) {
                        dvector.get(i).SetNextHop(nextHop.GetId());
                        int hops = 0;
                        int id = routerId;
                        int nid;
                        while(id != i) {
                            hops++;
                            nid = dvtable.GetCell(id,i).GetNextHop();
                            if(nid != i && id == dvtable.GetCell(nid,i).GetNextHop()) {
                                hops = -1;
                                break;
                            }
                            id = nid;
                        }
                        dvector.get(i).SetHops(hops);
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
}
