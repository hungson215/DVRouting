
public class DVCell {
    private int hops;
    private int cost;
    private int nexthop;
    private int dv;

    /**
     * Set Hops
     * @param hops
     */
    public void SetHops(int hops) {
        this.hops = hops;
    }

    /**
     * Get Hops
     * @return
     */
    public int GetHops() {
        return hops;
    }

    /**
     * Set next hop
     * @param nexthop
     */
    public void SetNextHop(int nexthop) {
        this.nexthop = nexthop;
    }

    /**
     * Get next hop
     * @return
     */
    public int GetNextHop() {
        return nexthop;
    }

    /**
     * Set Link Cost
     * @param cost
     */
    public void SetCost(int cost) {
        this.cost = cost;
    }

    /**
     * Get Cost
     * @return
     */
    public int GetCost() {
        return cost;
    }

    /**
     * Constructor
     * @param nexthop
     * @param hops
     * @param cost
     */
    public DVCell (int nexthop, int hops, int cost, int dv) {
        this.cost = cost;
        this.nexthop = nexthop;
        this.hops = hops;
        this.dv = dv;
    }

    /**
     * Constructor
     * @param cell
     */
    public DVCell (DVCell cell) {
        this.cost = cell.GetCost();
        this.nexthop = cell.GetNextHop();
        this.hops = cell.GetHops();
        this.dv = cell.GetDV();
    }
    public int GetDV(){
        return dv;
    }
    public void SetDV(int dv) {
        this.dv = dv;
    }
    /**
     * Default constructor
     */
    public DVCell() {
        cost = -1;
        nexthop = 0;
        hops = 0;
        dv = -1;
    }
}
