import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) throws IOException{
        BufferedReader f = new BufferedReader(new FileReader(args[0]));
        String str;
        int totalRouters = 0;
        HashMap<Integer,Router> network = new HashMap<>();
        HashMap<Integer,String> events = new HashMap<>();
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
            events.put(Integer.parseInt(str.substring(0,i)), str.substring(i+1));
        }

        int round = 1;
        boolean isConverge = false;
        int cti = round;
        boolean badvertise = false;
        boolean bupdate = true;
        while(true) {
            boolean flag = true;
            if(isConverge && events.isEmpty()) {
                System.out.println("All events are completed! The routing table is converged");
                break;
            } else if((round - cti) > 100) {
                System.out.println("Count to infinity problem");
                break;
            }
            if(events.containsKey(round)) {
                System.out.println("EVENT! Cost change happen");
                String[] tokens = events.get(round).split(" ");
                int rid1 = Integer.parseInt(tokens[0]);
                int rid2 = Integer.parseInt(tokens[1]);
                int cost = Integer.parseInt(tokens[2]);
                Router r1 = network.get(rid1);
                Router r2 = network.get(rid2);
                //Add new link
                if(r1.GetLinkCost(rid2) < 0 && cost > 0) {
                    r1.AddAdjacentRouter(r2,cost);
                    r2.AddAdjacentRouter(r1,cost);
                //Remove a link
                } else if (cost < 0) {
                    r1.RemoveAdjacentRouter(r2);
                    r2.RemoveAdjacentRouter(r1);
                //Change link cost
                } else {
                    r1.SetLinkCost(rid2, cost);
                    r2.SetLinkCost(rid1, cost);
                }
                events.remove(round);
                cti = round;
                bupdate = true;
                badvertise = false;
            } else if (isConverge) {
                System.out.println("Round " + round + ": Routing table is converged");
                round++;
                cti = round;
                continue;
            } else {
                badvertise = !badvertise;
                bupdate = !bupdate;
            }
            if(badvertise) {
                System.out.println("Round " + round + ": Advertise");
                for (Router r : network.values()) {
                    if (!r.IsConverge()) {
                        r.Advertise();
                    }
                }
                round++;
            }
            if(bupdate) {
                System.out.println("Round " + round + ": Update DVTable");
                for (Router r : network.values()) {
                    r.UpdateDateDVector();
                    //r.PrintDVector();
                    if (!r.IsConverge()) {
                        flag = false;
                    }
                }
                isConverge = flag;
                round++;
            }
        }
        for(Router r: network.values()) {
            r.PrintDVector();
        }
    }
}
