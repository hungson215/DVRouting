import java.util.HashMap;

public class DVTable {
    private HashMap<Integer,HashMap<Integer,DVCell>> dvtable;

    public DVTable(){
        dvtable = new HashMap<>();
    }
    public void SetCell(int rowid,int colid, DVCell cell) {
        if(rowid != colid) {
            //If rowid does not exist, add a new entry to the map
            if (!dvtable.containsKey(rowid)) {
                ExpandTable(rowid);
            }
            //If colid does not exist, add a new entry to the map
            if (!dvtable.get(rowid).containsKey(colid)) {
               ExpandTable(colid);
            }
        }
        dvtable.get(rowid).get(colid).SetCost(cell.GetCost());
        dvtable.get(rowid).get(colid).SetNextHop(cell.GetNextHop());
        dvtable.get(rowid).get(colid).SetHops(cell.GetHops());
        dvtable.get(rowid).get(colid).SetDV(cell.GetDV());
    }
    private void ExpandTable(int id) {
        HashMap<Integer, DVCell> entry = new HashMap<>();
        dvtable.put(id, entry);
        //Populate the entries of the new row
        for (Integer key : dvtable.keySet()) {
            entry.put(key, new DVCell());
            if(key!= id) {
                dvtable.get(key).put(id, new DVCell());
            } else {
                dvtable.get(key).put(id, new DVCell(key,0,0, 0));
            }
        }
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
    public void PrintTable() {
        System.out.println("__________________________________________________________");
        for(Integer i: dvtable.keySet()) {
            PrintDVector(i);
        }
    }
    public void PrintDVector(int id) {
        HashMap<Integer,DVCell> dvector = dvtable.get(id);
        for(Integer i : dvector.keySet()) {
            System.out.print("\t|\t\t" + i + "\t\t");
        }
        System.out.println();
        for(Integer i : dvector.keySet()) {
            System.out.print("\t|\tCost:");
            if(dvector.get(i).GetCost() == -1) {
                System.out.print("\tINFINITY\t");
            } else if (dvector .get(i).GetCost() == -2) {
                System.out.print("\tNaN\t\t");
            } else {
                System.out.print(dvector.get(i).GetCost());
                System.out.print("\t\t");
            }
        }
        System.out.println();
        System.out.print(id);
        for(Integer i : dvector.keySet()) {
            System.out.print("\t|\tDistance:");
            if(dvector.get(i).GetDV() == -1) {
                System.out.print("\tINFINITY\t");
            } else if (dvector.get(i).GetDV() == -2) {
                System.out.print("\tNaN\t");
            } else {
                System.out.print(dvector.get(i).GetDV());
                System.out.print("\t");
            }
        }
        System.out.println();
        for(Integer i : dvector.keySet()) {
            System.out.print("\t|\tNextHop:" + dvector.get(i).GetNextHop()+"\t");
        }
        System.out.println();
        for(Integer i : dvector.keySet()) {
            System.out.print("\t|\tHops:\t"+ dvector.get(i).GetHops() + "\t");
        }
        System.out.println();
        System.out.println("-----------------------------------------------------------");
    }
}