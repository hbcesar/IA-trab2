
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
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
        
        // instanciando o método de busca
        Search metodo = new MeuMetodo();

        // instanciando o problema        
        Problema p = new Problema("entrada/sonar.arff");
        
        // executando o método
        Result r = metodo.startSearch(p);   
        r.printResult();                
    }    
}
