import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Nguyen on 4/13/2017.
 */
public class DVTable {
    private HashMap<Integer,HashMap<Integer,DVCell>> dvtable;

    public void SetCell(int rowid,int colid, DVCell cell) {
        //If rowid does not exist, add a new entry to the map
        if(!dvtable.containsKey(rowid)) {
            //Add the new row to the table
            HashMap<Integer,DVCell> newrow = new HashMap<>();
            dvtable.put(rowid,newrow);
            //Populate the entries of the new row
            for(Integer key: dvtable.keySet()) {
                newrow.put(key,new DVCell());
                dvtable.get(key).put(rowid,new DVCell());
            }
        }
        //If colid does not exist, add a new entry to the map
        if(!dvtable.get(rowid).containsKey(colid)) {
            HashMap<Integer,DVCell> newrow = new HashMap<>();
            dvtable.put(colid,newrow);
            //Populate the entries of the new row
            for(Integer key: dvtable.keySet()) {
                newrow.put(key,new DVCell());
                dvtable.get(key).put(colid,new DVCell());
            }
        }
        dvtable.get(rowid).get(colid).SetCost(cell.GetCost());
        dvtable.get(rowid).get(colid).SetNextHop(cell.GetNextHop());
        dvtable.get(rowid).get(colid).SetHops(cell.GetHops());
    }

    /**
     * Add new DVector to the table
     * @param id
     * @param dv
     */
    public void AddDVector(int id, HashMap<Integer,DVCell> dv) {
        for(Integer i: dv.keySet()) {
            SetCell(id,i, dv.get(i));
        }
    }

    /**
     * Get DVector
     * @param id
     * @return
     */
    public HashMap<Integer,DVCell> GetDVector(int id){
        return dvtable.get(id);
    }

    /**
     * Get DVCell
     * @param rowid
     * @param colid
     * @return
     */
    public DVCell GetCell(int rowid, int colid) {
        return  dvtable.get(rowid).get(colid);
    }
}