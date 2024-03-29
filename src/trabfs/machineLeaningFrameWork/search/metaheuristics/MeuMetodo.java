/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabfs.machineLeaningFrameWork.search.metaheuristics;

import trabfs.machineLeaningFrameWork.core.AvaliadordeSolucao;
import trabfs.machineLeaningFrameWork.core.Problema;
import trabfs.machineLeaningFrameWork.core.Result;
import trabfs.machineLeaningFrameWork.core.Solucao;
import trabfs.machineLeaningFrameWork.search.Search;
import weka.core.Debug.Random;

/**
 *
 * @author rbroetto
 */
public class MeuMetodo extends Search{
    private int sizepopulation;
    private double erroMin;
    private int maxChanges;
    private Solucao best = null;
    private Solucao[] populacao;
    private Solucao[] localBest;
    private double[][] velocity;
    
    @Override
    public Result startSearch(Problema p) {
        //----------------------------------- inicializa variaveis
        //parametro 1: numero de particulas
        //parametro 2: erro minimo
        //parametro 3: numero minimo de iteracoes sem mudanca significativa do erro
        //parametro 4: p recebido pelo metodo
        this.inicializarVariaveis(10, 0.0000000001, 20, p);
        
        long t = System.currentTimeMillis();
        
        // ---------------------------------- [inicio do metodo de busca]
        
        //Iniciar as populacoes
        this.start();
        
        //Faz calculo das velocidades e da nova posicao até que mudanca de qualidade seja minima
        double erro;
        int iteracoes = 0;
        int mudancas = 0;
        
        while(mudancas <= this.maxChanges && iteracoes < 1000){
            //Guarda melhor solucao antes dessa iteracao pra poder calcular o erro
            Solucao oldBest = new Solucao(best);
            
            //atualiza posicoes das particulas
            this.atualizarSwarm();
            
            //avalia novas posicoes
            this.avaliar();
            
            //Calcula o erro
            erro = best.getQuality() - oldBest.getQuality();
            erro = Math.abs(erro);
            
            //Verifica se erro é minimo 20 vezes consecutivas
            if(erro <= this.erroMin){
                mudancas++;
            } else {
                mudancas = 0;
            }
            
            //Atualiza nmr de iteracoes
            iteracoes++;
        }
            
        // ---------------------------------- [fim do método de busca]
        
        t = System.currentTimeMillis() - t;
        Result r = new Result();
        r.setSolucao(best);
        r.setTime(t);
        r.setMetodo(this.getClass().getName());
        r.setDataset(p.getInstances().relationName());
        r.setCalls(as.getCalls());
        r.setEvolucao(as.getEvolucao());
        return r;
    }
    
    private void inicializarVariaveis(int sizepopulation, double erroMin, int maxChanges, Problema problema){
        this.sizepopulation = sizepopulation;
        this.erroMin = erroMin;
        this.N = problema.getNumAtributos()-1;
        this.as = new AvaliadordeSolucao(problema);
        this.populacao = new Solucao[this.sizepopulation];
        this.localBest = new Solucao[this.sizepopulation];
        this.velocity = new double[this.sizepopulation][this.N];
        this.maxChanges = maxChanges;
        this.best = new Solucao(this.N);
        this.best.initRandom();
    }
    
    
    private void start(){
        for (int i = 0; i < this.sizepopulation; i++){
            Solucao sol = new Solucao(N);
            sol.initRandom();
            populacao[i] = sol;
            localBest[i] = sol;
            
            this.as.avalia(sol);
            
            if(sol.getQuality() > this.best.getQuality()){
                best = new Solucao(sol);                
            }
        }
    }

    private void atualizarSwarm(){
        //Pesos
        double e1 = rand();
        double e2 = rand();
        
        //Velocidade máxima
        //Valor indicado por (Kennedy & Eberhat, 1997)
        double vmax = 6;
        
        for (int i = 0; i < this.sizepopulation; i++){
            //velocidade é calculada bit a bit
            for(int j = 0; j < N; j++){
                double cognitive;
                double social;
            
                cognitive = e1 * (localBest[i].get(j) - populacao[i].get(j));
                social = e2 * (best.get(j) - populacao[i].get(j));
                
                velocity[i][j] = velocity[i][j] + cognitive + social;
                
                //verifica os limites max e min da velocidade
                if (Math.abs(velocity[i][j]) > vmax){
                    if(velocity[i][j] > 0) {
                        //upperbound
                        velocity[i][j] = vmax;
                    } else {
                        //lowerbound
                        velocity[i][j] = -vmax;
                    }
                }
                
                //calcula o sigmoide (para atualizar posicao corretamente)
                velocity[i][j] = sigmoid(velocity[i][j]);
                
                //atualiza a posicao
                if(rand() < velocity[i][j]){
                    populacao[i].set(j, 1);
                } else {
                    populacao[i].set(j, 0);
                }
            }
        }
    }
    
    private void avaliar(){
        for (int i = 0; i < this.sizepopulation; i++){
            //avalia nova solucao
            as.avalia(populacao[i]);
            
            //atualiza o melhor local
            if(populacao[i].getQuality() > localBest[i].getQuality()){
                localBest[i] = new Solucao(populacao[i]);                
            }
            
            //atualiza o melhor global
            if(populacao[i].getQuality() > best.getQuality()){
                best = new Solucao(populacao[i]);                
            }
        }
    }
    
    private double sigmoid(double x){
        return 1.0/(1.0 + Math.exp(x));
    }
    
    // Retorna número real no intervalo [0, 1]
    private double rand() {
        Random r = new Random();
        return 0 + (1 - 0) * r.nextDouble();
    }
} 