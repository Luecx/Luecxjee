package luecx.data.mnist;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MnistSet {
    List<MnistNumber> set;
    MnistSet(File imageFile, File labelFile ){
        DataInputStream imageStream ;
        DataInputStream labelStream ;
        try {
            imageStream = new DataInputStream( new BufferedInputStream( new FileInputStream(imageFile)));
            labelStream = new DataInputStream( new BufferedInputStream( new FileInputStream(labelFile)));
        } catch (FileNotFoundException e) {
            throw new Error(e);
        }
        try {
            int magicNumber;
            magicNumber = imageStream.readInt();
            if ( magicNumber != 2051 ) throw new Error("bad magic number "+magicNumber+"for file "+imageFile.getName());
            magicNumber = labelStream.readInt();
            if ( magicNumber != 2049 ) throw new Error("bad magic number "+magicNumber+" for file "+labelFile.getName());

            int nbLabels = labelStream.readInt();
            int nbImages = imageStream.readInt();
            if ( nbImages != nbLabels ) throw new Error("nb image != nb label : "+nbImages+"=!"+nbLabels);
            System.err.println("nb Items = "+ nbImages);
            set = new ArrayList<>(nbImages);
            int nbRows = imageStream.readInt();
            int nbCols = imageStream.readInt();
            if ( nbCols != 28 || nbRows != 28 ) throw new Error("image size != 28x28:"+nbRows+"x"+nbCols);
            for ( int i = 0 ; i< nbImages ; i++ ) set.add(new MnistNumber(labelStream,imageStream, nbRows,nbCols));
        } catch (IOException e) {
            throw new Error (e);
        }
    }
}
