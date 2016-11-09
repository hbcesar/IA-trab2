/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabfs.machineLeaningFrameWork.search.metaheuristics;

import java.util.ArrayList;
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
    
//    protected Problema p;    
    private Solucao best = null;
    private Solucao[] populacao;
    private Solucao[] localBest;
    private double[][] velocity;
    
    @Override
    public Result startSearch(Problema p) {
        //inicializa variaveis
        //parametro 1: numero de particulas
        //parametro 2: erro minimo
        //parametro 3: p recebido pelo metodo
        this.inicializarVariaveis(20, 0.0000000001, p);
        
        long t = System.currentTimeMillis();
        
        // ---------------------------------- [inicio do metodo de busca]
        
        /*
        TODO:
        - Verificar condicao de parada - OK?
        - Verificar o rand - OK
        - Modularizar - OK
        */
        
        //Iniciar as populacoes
        this.start();
        
        //Faz calculo das velocidades e da nova posicao até que mudanca de qualidade seja minima
        double erro = Double.MAX_VALUE;
        while(erro > erroMin){
            Solucao oldBest = new Solucao(best);
            
            this.atualizarSwarm();
            this.avaliar();
            
            //Calcula o fitness
            erro = best.getQuality() - oldBest.getQuality();
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
    
    private void inicializarVariaveis(int sizepopulation, double erroMin, Problema problema){
        this.sizepopulation = sizepopulation;
        this.erroMin = erroMin;
        this.N = problema.getNumAtributos()-1;
        this.as = new AvaliadordeSolucao(problema);
        this.populacao = new Solucao[this.sizepopulation];
        this.localBest = new Solucao[this.sizepopulation];
        this.velocity = new double[this.sizepopulation][this.N];
    }
    
    
    private void start(){
        for (int i = 0; i < this.sizepopulation; i++){
            Solucao sol = new Solucao(N);
            sol.initRandom();
            populacao[i] = sol;
            localBest[i] = sol;
            
            if(best == null){
                best = new Solucao(sol);
            }
            
            as.avalia(sol);
            
            if(sol.getQuality() > best.getQuality()){
                best = new Solucao(sol);                
            }
        }
    }

    private void atualizarSwarm(){
        Random rand = new Random();
        
        double c = 2.5;
        double e1 = rand.nextDouble();
        double e2 = rand.nextDouble();
        double vmax = 6;
        
        for (int i = 0; i < this.sizepopulation; i++){
            //velocidade é calculada bit a bit
            for(int j = 0; j < N; j++){
                double cognitive;
                double social;
            
                cognitive = c * e1 * (localBest[i].get(j) - populacao[i].get(j));
                social = c * e2 * (best.get(j) - populacao[i].get(j));
                
                velocity[i][j] = velocity[i][j] + cognitive + social;
                
                //verifica os limites max e min da velocidade
                if (Math.abs(velocity[i][j]) > vmax && Math.abs(velocity[i][j]) == velocity[i][j]){
                    velocity[i][j] = vmax;
                } else {
                    velocity[i][j] = -vmax;
                }
                
                //calcula o sigmoide (para atualizar posicao corretamente)
                velocity[i][j] = sigmoid(velocity[i][j]);
                
                //atualiza a posicao
                if(rand.nextDouble() < velocity[i][j]){
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
    
    private int fitness(Solucao s1, Solucao s2){
        int dist = 0;
        
        for(int i =0; i < N; i++){
            if(s1.get(1) != s2.get(i))
                dist++;
        }
        
        return dist;
    }
} 