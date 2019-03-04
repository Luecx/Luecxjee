package luecx.ai.genetic_algorithm.neuro_evolution.def;


import luecx.ai.neuralnetwork.layers.DenseLayer;
import luecx.ai.neuralnetwork.layers.Layer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class GeneticAlgorithm {

    public double MUTATION_RATE = 0.1;
    public double MUTATION_STENGTH = 0.1;
    public double AMOUNT_SURVIVORS = 1;

    public <T extends GeneticClient> void evolve(ArrayList<T> clients){
        clients.sort(new Comparator<GeneticClient>() {
            @Override
            public int compare(GeneticClient o1, GeneticClient o2) {
                if(o1.getScore() > o2.getScore()) return -1;
                if(o2.getScore() > o1.getScore()) return 1;
                return 0;
            }
        });

        //printClients(clients);
        ArrayList<T> selection = selection(clients);
        crossover(clients, selection);
        mutate(clients);
        merge(clients, selection);
    }

    public static <T extends GeneticClient> void printClients(ArrayList<T> clients){
        int index = 0;
        for(T t:clients){
            index++;
            System.out.format("index %3s     score %-20s alive: %-10s \n", index, t.getScore(), t.getNetwork());
        }
    }

    protected <T extends GeneticClient> ArrayList<T> selection(ArrayList<T> clients){
        ArrayList<T> selection = new ArrayList<>();

        for(int i = 0; i < Math.min(AMOUNT_SURVIVORS, clients.size()); i++){
            selection.add(clients.get(i));
        }
        for(GeneticClient g:selection){
            clients.remove(g);
        }

        return selection;
    }

    protected <T extends GeneticClient> void merge(ArrayList<T> newborns, ArrayList<T> survivors){
        newborns.addAll(survivors);
    }

    protected <T extends GeneticClient> void crossover(ArrayList<T> clients,ArrayList<T> selection){

        double parentSum = 0;
        for(GeneticClient c:selection){
            parentSum += c.getScore();
        }


        for(GeneticClient g:clients){

            double r = Math.random();
            double c = 0;
            GeneticClient parent = null;
            for(GeneticClient sel:selection){
                c += (sel.getScore() + 0.0001) / (parentSum + 0.0001);
                if(r < c){
                    parent = sel;
                    break;
                }
            }

            Layer left = g.getNetwork().getInputLayer();
            Layer right = parent.getNetwork().getInputLayer();

            while(left != g.getNetwork().getOutputLayer()){
                left = left.getNext_layer();
                right = right.getNext_layer();
                if(left instanceof DenseLayer && right instanceof DenseLayer){
                    ((DenseLayer) left).setWeights(copyArray(((DenseLayer) right).getWeights()));
                    ((DenseLayer) left).setBias(copyArray(((DenseLayer) right).getBias()));
                }
            }

        }
    }

    protected <T extends GeneticClient> void mutate(ArrayList<T> clients){
        for(GeneticClient g:clients){

            Layer l = g.getNetwork().getInputLayer();

            while(l != g.getNetwork().getOutputLayer()){
                l = l.getNext_layer();
                if(l instanceof DenseLayer){
                    mutateArray(((DenseLayer) l).getWeights(), MUTATION_RATE, MUTATION_STENGTH);
                    mutateArray(((DenseLayer) l).getBias(), MUTATION_RATE, MUTATION_STENGTH);
                }
            }
        }
    }



    public static void mutateArray(double[][] vals, double rate, double strength) {
        Random random = new Random();
        for (int i = 0; i < vals.length; i++) {
            for (int n = 0; n < vals[0].length; n++) {
                if (Math.random() < rate) {
                    vals[i][n] += random.nextGaussian() * strength;
                }
            }
        }
    }

    public static void mutateArray(double[] vals, double rate, double strength) {
        Random random = new Random();
        for (int i = 0; i < vals.length; i++) {
            if (Math.random() < rate) {
                vals[i] += random.nextGaussian() * strength;
            }
        }
    }

    public static double[][] copyArray(double[][] vals) {
        double[][] out = new double[vals.length][vals[0].length];
        for (int i = 0; i < vals.length; i++) {
            for (int n = 0; n < vals[0].length; n++) {
                out[i][n] = vals[i][n];
            }
        }
        return out;
    }

    public static double[] copyArray(double[] vals) {
        double[] out = new double[vals.length];
        for (int i = 0; i < vals.length; i++) {
            out[i] = vals[i];
        }return out;
    }


}
