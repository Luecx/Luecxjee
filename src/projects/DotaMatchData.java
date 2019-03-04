package projects;

import luecx.ai.neuralnetwork.Network;
import luecx.ai.neuralnetwork.data.TrainSet;
import luecx.ai.neuralnetwork.layers.Layer;
import luecx.ai.neuralnetwork.tools.ArrayTools;

import java.io.IOException;
import java.util.Arrays;

public class DotaMatchData {


    static class GameData {


        long match_id;
        long match_seq_num;
        boolean radiant_win;
        long start_time;
        long duration;
        int tower_status_radiant;
        int tower_status_dire;
        int barracks_status_radiant;
        int barracks_status_dire;
        int cluster;
        int first_blood_time;
        int lobby_type;
        int human_players;
        int leagueid;
        int positive_votes;
        int negative_votes;
        int game_mode;
        int engine;
        int picks_bans;
        int parse_status;
        int chat;
        int objectives;
        int radiant_gold_adv;
        int radiant_xp_adv;
        int teamfights;
        int version;
        int pgroup;


        int[] radiant_heroes;
        int[] dire_heroes;

        @Override
        public String toString() {
            return "GameData{" +
                    "\n match_id=" + match_id +
                    "\n match_seq_num=" + match_seq_num +
                    "\n radiant_win=" + radiant_win +
                    "\n start_time=" + start_time +
                    "\n duration=" + duration +
                    "\n tower_status_radiant=" + tower_status_radiant +
                    "\n tower_status_dire=" + tower_status_dire +
                    "\n barracks_status_radiant=" + barracks_status_radiant +
                    "\n barracks_status_dire=" + barracks_status_dire +
                    "\n cluster=" + cluster +
                    "\n first_blood_time=" + first_blood_time +
                    "\n lobby_type=" + lobby_type +
                    "\n human_players=" + human_players +
                    "\n leagueid=" + leagueid +
                    "\n positive_votes=" + positive_votes +
                    "\n negative_votes=" + negative_votes +
                    "\n game_mode=" + game_mode +
                    "\n engine=" + engine +
                    "\n picks_bans=" + picks_bans +
                    "\n parse_status=" + parse_status +
                    "\n chat=" + chat +
                    "\n objectives=" + objectives +
                    "\n radiant_gold_adv=" + radiant_gold_adv +
                    "\n radiant_xp_adv=" + radiant_xp_adv +
                    "\n teamfights=" + teamfights +
                    "\n version=" + version +
                    "\n pgroup=" + pgroup +
                    "\n radiant_heroes=" + Arrays.toString(radiant_heroes) +
                    "\n dire_heroes=" + Arrays.toString(dire_heroes) +
                    '}';
        }

        public GameData(String line) {
            String[] ar = line.split(",");

            match_id = Long.parseLong(ar[0]);
            match_seq_num = Integer.parseInt(ar[1]);
            radiant_win = ar[2].equals("t");
            start_time = Integer.parseInt(ar[3]);
            duration = Integer.parseInt(ar[4]);
            tower_status_radiant = Integer.parseInt(ar[5]);
            tower_status_dire = Integer.parseInt(ar[6]);
            barracks_status_radiant = Integer.parseInt(ar[7]);
            barracks_status_dire = Integer.parseInt(ar[8]);
            cluster = Integer.parseInt(ar[9]);
            first_blood_time = Integer.parseInt(ar[10]);
            lobby_type = Integer.parseInt(ar[11]);
            human_players = Integer.parseInt(ar[12]);
            leagueid = Integer.parseInt(ar[13]);
            positive_votes = Integer.parseInt(ar[14]);
            negative_votes = Integer.parseInt(ar[15]);
            game_mode = Integer.parseInt(ar[16]);
            engine = Integer.parseInt(ar[17]);

            radiant_heroes = new int[5];
            dire_heroes = new int[5];

            int c = 0;
            for (int i = 10; i < ar.length; i++) {
                if (ar[i].contains("\"\"hero_id\"\"")) {
                    String[] split = ar[i].split(":");
                    int id = Integer.parseInt(split[split.length - 1]);
                    if (c < 5) {
                        radiant_heroes[c] = id;
                    } else {
                        dire_heroes[c - 5] = id;
                    }
                    c++;
                }
            }
        }

        public long getMatch_id() {
            return match_id;
        }

        public long getMatch_seq_num() {
            return match_seq_num;
        }

        public boolean isRadiant_win() {
            return radiant_win;
        }

        public long getStart_time() {
            return start_time;
        }

        public long getDuration() {
            return duration;
        }

        public int getTower_status_radiant() {
            return tower_status_radiant;
        }

        public int getTower_status_dire() {
            return tower_status_dire;
        }

        public int getBarracks_status_radiant() {
            return barracks_status_radiant;
        }

        public int getBarracks_status_dire() {
            return barracks_status_dire;
        }

        public int getCluster() {
            return cluster;
        }

        public int getFirst_blood_time() {
            return first_blood_time;
        }

        public int getLobby_type() {
            return lobby_type;
        }

        public int getHuman_players() {
            return human_players;
        }

        public int getLeagueid() {
            return leagueid;
        }

        public int getPositive_votes() {
            return positive_votes;
        }

        public int getNegative_votes() {
            return negative_votes;
        }

        public int getGame_mode() {
            return game_mode;
        }

        public int getEngine() {
            return engine;
        }

        public int getPicks_bans() {
            return picks_bans;
        }

        public int getParse_status() {
            return parse_status;
        }

        public int getChat() {
            return chat;
        }

        public int getObjectives() {
            return objectives;
        }

        public int getRadiant_gold_adv() {
            return radiant_gold_adv;
        }

        public int getRadiant_xp_adv() {
            return radiant_xp_adv;
        }

        public int getTeamfights() {
            return teamfights;
        }

        public int getVersion() {
            return version;
        }

        public int getPgroup() {
            return pgroup;
        }

        public int[] getRadiant_heroes() {
            return radiant_heroes;
        }

        public int[] getDire_heroes() {
            return dire_heroes;
        }

        public static TrainSet generate_trainset_v1(GameData[] games) {
            TrainSet t = new TrainSet(1, 2, 116, 1, 1, 1);
            for (GameData d : games) {
                double[][][] in = new double[1][2][116];
                for (int i = 0; i < 5; i++) {
                    in[0][0][d.radiant_heroes[i]] = 1;
                    in[0][1][d.dire_heroes[i]] = 1;
                }
                t.addData(in, ArrayTools.createComplexFlatArray(d.radiant_win ? 1 : 0));
            }
            return t;
        }
    }

    public static void main(String[] args) throws IOException {




        Network net = Network.load_network("res/dota_prediction_network");

        double[][][] in = new double[1][2][116];
        for(int i = 1; i < 100; i++){
            in[0][0][i] = 1;
            Layer.printArray(net.calculate(in));

            in[0][0][i] = 0;
        }

//        BufferedReader reader = new BufferedReader(new FileReader(new File("D:\\Downloads\\matches_small.csv")));
//        reader.readLine();
//        GameData[] train_games = new GameData[30000];
//        GameData[] val_games = new GameData[10000];
//
//        for (int i = 0; i < train_games.length; i++) {
//            train_games[i] = new GameData(reader.readLine());
//        }
//        for (int i = 0; i < val_games.length; i++) {
//            val_games[i] = new GameData(reader.readLine());
//        }
//        reader.close();
//
//        TrainSet t = GameData.generate_trainset_v1(train_games);
//        TrainSet v = GameData.generate_trainset_v1(val_games);
//
//        NetworkBuilder builder = new NetworkBuilder(1, 2, 116);
//        builder.addLayer(new TransformationLayer());
//        builder.addLayer(new DenseLayer(100));
//        builder.addLayer(new DenseLayer(40));
//        builder.addLayer(new DenseLayer(1));
//        Network network = builder.buildNetwork();
//
//        network.train(t, 20, 100, 10);
//        network.validate_binary(v);
//        network.save_network("dota_prediction_network");
    }

}
