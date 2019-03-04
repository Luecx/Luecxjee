package luecx.data.mnist_streamer;

import java.io.DataInputStream;
import java.io.IOException;

public class MnistNumber {
    int nbRows;
    int nbCols;
    int pixels[];
    String label;
    MnistNumber(DataInputStream labelStream, DataInputStream imageStream,int nbRows,int nbCols){
        this.nbCols=nbCols;
        this.nbRows=nbRows;
        try {
            label = String.valueOf( labelStream.readUnsignedByte() );

            pixels = new int[nbRows*nbCols];
            for ( int i=0 ; i< (nbRows*nbCols) ; i++ ) pixels[i]=imageStream.readUnsignedByte();
        } catch (IOException e) {
            throw new Error(e);
        }
    }
}
