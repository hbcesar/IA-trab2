
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import trabfs.machineLeaningFrameWork.core.Problema;
import trabfs.machineLeaningFrameWork.core.Result;
import trabfs.machineLeaningFrameWork.search.Search;
import trabfs.machineLeaningFrameWork.search.metaheuristics.GeneticSearch;
import trabfs.machineLeaningFrameWork.search.metaheuristics.MeuMetodo;
/**
 *
 * @author rbroetto
 */
public class Do {
    
    public static void main(String[] args){
        File folder = new File("entrada");
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                Search metodo = new MeuMetodo();
                Problema p = new Problema("entrada/" + listOfFiles[i].getName());
                Result r = metodo.startSearch(p); 
                
                r.printResult();
            }
        }
    
//        // instanciando o método de busca
//        Search metodo = new MeuMetodo();
//
//        // instanciando o problema        
//        Problema p = new Problema("entrada/sonar.arff");
//        
//        // executando o método
//        Result r = metodo.startSearch(p);   
//        r.printResult();                
    }    
}
