import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException{
        BufferedReader f = new BufferedReader(new FileReader(args[0]));
        String str;
        int totalRouters = 0;
        int convergeDelay = 0;
        Router.METHOD method;
        int detail = Integer.parseInt(args[2]);
        HashMap<Integer,Router> network = new HashMap<>();
        HashMap<Integer,ArrayList<String>> events = new HashMap<>();
        while((str = f.readLine()) != null) {
            if(totalRouters == 0) {
                totalRouters = Integer.parseInt(str);
            } else {
                String[] tokens = str.split(" ");
                int rid1 = Integer.parseInt(tokens[0]);
                int rid2 = Integer.parseInt(tokens[1]);
                int cost = Integer.parseInt(tokens[2]);
                Router r1, r2;
                if(network.containsKey(rid1)) {
                    r1 = network.get(rid1);
                } else {
                    r1 = new Router(rid1);
                }
                if(network.containsKey(rid2)) {
                    r2 = network.get(rid2);
                } else {
                    r2 = new Router(rid2);
                }
                r1.AddAdjacentRouter(r2, cost);
                r2.AddAdjacentRouter(r1, cost);
                network.put(rid1,r1);
                network.put(rid2,r2);
            }
        }
        f.close();
        f = new BufferedReader(new FileReader(args[1]));
        while((str = f.readLine()) != null) {
            int i = str.indexOf(' ');
            ArrayList<String> eList;
            int index = Integer.parseInt(str.substring(0,i));
            if(events.containsKey(index)) {
                eList = events.get(index);
                eList.add(str.substring(i+1));
            } else {
                eList = new ArrayList<>();
                eList.add(str.substring(i+1));
                events.put(index,eList);
            }
        }
        Scanner scan = new Scanner(System.in);
        System.out.println("--Routing method--");
        System.out.println("1. Basic");
        System.out.println("2. Split horizon");
        System.out.println("3. Poison Reverse");
        System.out.print("Select the method[1-3]:");
        int input = Integer.parseInt(scan.nextLine());
        switch(input) {
            case 2:
                method = Router.METHOD.SPLIT_HORIZON;
                break;
            case 3:
                method = Router.METHOD.POISON_REVERSE;
                break;
            default:
                method = Router.METHOD.BASIC;
        }
        int round = 1;
        boolean isConverge = false;
        boolean cti = false;
        System.out.println("------------------------");
        System.out.println("Method used: " + method);
        System.out.println("Print table each round: " + ((detail == 1) ? "Yes" : "No"));
        System.out.println("-------------------------");
        while(true) {
            boolean flag = true;
            System.out.print("Start Round " + round);
            convergeDelay++;
            //If there's no event and the DVTable is converged, stop the program
            if(isConverge && events.isEmpty()) {
                System.out.println(": Table converged");
                System.out.println("Convergence Delay: " + convergeDelay + " rounds");
                break;
            } else if(cti){
                System.out.println(": Count To Infinity Problem Encountered");
                break;
            }
            //Advertise
            if(!isConverge){
                for (Router r : network.values()) {
                    r.Advertise(method, false);
                }
            }
            if(detail == 1) {
                for(Router r : network.values()) {
                    r.PrintDVector();
                }
            }

            //Check for any event
            if(events.containsKey(round)) {
                convergeDelay = 0;
                System.out.println(" : EVENT!!");
                for(String event : events.get(round)) {
                    String[] tokens = event.split(" ");
                    int rid1 = Integer.parseInt(tokens[0]);
                    int rid2 = Integer.parseInt(tokens[1]);
                    int cost = Integer.parseInt(tokens[2]);
                    System.out.println("New cost: " + rid1 +" -> " +rid2+": " +cost);
                    Router r1 = network.get(rid1);
                    Router r2 = network.get(rid2);
                    //Add new link
                    if (r1.GetLinkCost(rid2) < 0 && cost > 0) {
                        r1.AddAdjacentRouter(r2, cost);
                        r2.AddAdjacentRouter(r1, cost);
                        //Remove a link
                    } else if (cost < 0) {
                        r1.RemoveAdjacentRouter(r2);
                        r2.RemoveAdjacentRouter(r1);
                        r1.UpdateDateDVector();
                        r2.UpdateDateDVector();
                        r1.Advertise(method, true);
                        r2.Advertise(method, true);
                        //Change link cost
                    } else {
                        r1.SetLinkCost(rid2, cost);
                        r2.SetLinkCost(rid1, cost);
                    }
                }
                events.remove(round);
            } else if (isConverge) {
                System.out.println(" : Routing table converged");
                round++;
                continue;
            }
            //Update DVTable
            for (Router r : network.values()) {
                r.UpdateDateDVector();
                if(detail == 1 ) {
                    r.PrintDVector();
                }
                if (!r.IsConverge()) {
                    flag = false;
                } else if (r.IsCTI()){
                    cti = true;
                }
            }
            isConverge = flag;
            round++;
            System.out.println();
        }
        //Print DVTable after finishing
        for(Router r: network.values()) {
            r.PrintDVector();
        }
    }
}
