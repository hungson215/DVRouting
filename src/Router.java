import java.util.ArrayList;

public class Router {
    private int routerId; // router Id
    private ArrayList<ArrayList<Integer>> dvectors; //Distance Vector Table
    private ArrayList<Router> adjacentRouter; // Adjacent Routers
    private boolean isConverge;

    /**
     * Constructor
     * @param id
     */
    public Router(int id) {
        isConverge = false;
        routerId = id;
        dvectors = new ArrayList<>();
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
            r.AddDVector(routerId,dvectors.get(routerId));
        }
    }

    /**
     * Add/Update Distance Vector
     * (It does not check if the the new DV is from an Adjacent Router)
     * @param id
     * @param dvector
     */
    public void AddDVector(int id, ArrayList<Integer>dvector) {
        dvectors.set(id,dvector);
    }

    /**
     * Set/Update the link cost between 2 routers
     * @param id
     * @param cost
     */
    public void SetLinkCost(int id, int cost) {
        dvectors.get(routerId).set(id,cost);
        dvectors.get(id).set(routerId,cost);
    }

    /**
     * Get the link cost between 2 routers
     * @param id
     * @return
     */
    public int GetLinkCost(int id) {
        return dvectors.get(routerId).get(id);
    }

    /**
     * Add an adjacent router
     * @param router
     * @param cost
     */
    public void AddAdjacentRouter(Router router, int cost) {
        adjacentRouter.add(router);
        SetLinkCost(router.GetId(),cost);
    }

    /**
     * Update the DV
     */
    public void UpdateDateDVector(){
        //If the DV table is converged, no need to update
        if(isConverge) {
            return;
        }
        ArrayList<Integer> dvector = dvectors.get(routerId);
        boolean isUpdate = false;
        //Find the least cost path to any router in the network
        for(int i = 0; i< dvector.size(); i++) {
            if(i!= routerId) {
                int min = 0;
                for(Router r : adjacentRouter) {
                    int cost = dvector.get(r.GetId()) + r.GetLinkCost(i);
                    if(min < cost) {
                        min = cost;
                    }
                }
                // If the link cost is different, update it
                if(dvector.get(i) != min) {
                    dvector.set(i, min);
                    isUpdate = true;
                }
            } else {
                dvector.set(i,0); // set the link cost to itself to 0
            }
        }
        //Update the DV
        dvectors.set(routerId,dvector);
        //If nothing change then the DV table is converged
        isConverge = !isUpdate;
    }
}
