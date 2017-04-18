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
        for(Router r : adjacentRouter) {
            r.AddDVector(routerId,dvtable.GetDVector(routerId));
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
    }
    /**
     * Add an adjacent router
     * @param r
     * @param cost
     */
    public void AddAdjacentRouter(Router r, int cost) {
        adjacentRouter.add(r);
        dvtable.SetCell(routerId,r.GetId(),new DVCell(r.GetId(),1,cost));
    }

    public int GetLinkCost(int id) {
        return dvtable.GetCell(routerId,id).GetCost();
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
            if(i == routerId) {
                dvector.get(i).SetCost(0);
                dvector.get(i).SetNextHop(i);
                dvector.get(i).SetHops(0);
            } else {
                int min = dvector.get(i).GetCost();
                Router nextHop = this;
                for(Router r : adjacentRouter) {
                    int cost = dvector.get(r.GetId()).GetCost() + dvtable.GetCell(r.GetId(),i).GetCost();
                    if(min < cost) {
                        min = cost;
                        nextHop = r;
                    }
                }
                // If the link cost is different, update it
                if(dvector.get(i).GetCost() != min) {
                    dvector.get(i).SetCost(min);
                    dvector.get(i).SetNextHop(nextHop.GetId());
                    dvector.get(i).SetHops(dvtable.GetCell(nextHop.GetId(),i).GetHops());
                    isUpdate = true;
                }
            }
        }
        //If nothing change then the DV table is converged
        isConverge = !isUpdate;
    }
}