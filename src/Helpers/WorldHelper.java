package Helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import GeneticAlgorithm.Config;
import GeneticAlgorithm.Individual;
import GeneticAlgorithm.Utility;

public class WorldHelper {
    private static Random random = new Random();

    public static List<Individual> SpawnPopulation(){
        List<Individual> population = new ArrayList<Individual>();

        // Generate {PopulationCount} individuals
        while (population.size() < Config.populationCounts)
        {
            Individual individual = GenerateIndividual(Config.numberOfCities);
            if (!population.contains(individual))
            {
                population.add(individual);
            }
        }

        return population;
    }

    public static Individual GenerateIndividual(int sequenceLength){   
        List<Integer> sequence = new ArrayList<Integer>();
        
        for (int i = 0; i < Config.numberOfCities; i++){
            sequence.add(i);
        }

        Utility.randomize(sequence);

        return new Individual(sequence);
    }

    public static List<Individual> GetCandidateParents(List<Individual> population){
        // Grab two random individuals from the population
        Individual candidateA = population.get(random.nextInt(population.size()));
        Individual candidateB = population.get(random.nextInt(population.size()));

        // Ensure that the two individuals are unique
        while (candidateA == candidateB)
        {
            candidateB = population.get(random.nextInt(population.size()));
        }

        ArrayList<Individual> returnOffspring = new ArrayList<Individual>();
        returnOffspring.add(candidateA);
        returnOffspring.add(candidateB);

        return returnOffspring;
    }

    public static Individual tournamentSelection(Individual candidateA, Individual candidateB)
    {
        if (candidateA.rank < candidateB.rank){

            return candidateA;

        } else if (candidateA.rank == candidateB.rank){

            return candidateA.crowdingDistance > candidateB.crowdingDistance ? candidateA : candidateB;

        } else{

            return candidateB;
        }
    }

    public static Individual DoCrossover(Individual individualA, Individual individualB){
        return DoCrossover(individualA, individualB, -1);
    }

    public static Individual DoCrossover(Individual individualA, Individual individualB, int crossoverPosition)
    {
        crossoverPosition = crossoverPosition == -1 
            ? 1 + random.nextInt(individualA.sequence.size() - 2)
            : crossoverPosition;

        List<Integer> offspringSequence = new ArrayList<Integer>();
        for (int i = 0; i < crossoverPosition; i++){
            offspringSequence.add(individualA.sequence.get(i));
        }

        List<Integer> appeared = new ArrayList<Integer>();
        for (int i = 0; i < offspringSequence.size(); i++){
            appeared.add(offspringSequence.get(i));
        }

        for (int town : individualB.sequence){
            if (appeared.contains(town)){
                continue;
            }

            offspringSequence.add(town);
        }

        return new Individual(offspringSequence);
    }

    public static int[] GetUniqueTowns(List<Integer> sequence){
        // Randomly select two towns
        int townA = random.nextInt(sequence.size());
        int townB = random.nextInt(sequence.size());

        // Ensure that the two towns are not the same
        while (townB == townA)
        {
            townB = random.nextInt(sequence.size());
        }

        return new int[]{townA, townB};
    }

    public static Individual DoRotateMutate(Individual individual)
    {
        int[] towns = GetUniqueTowns(individual.sequence);

        int firstIndex = towns[0] < towns[1] ?  towns[0] : towns[1];
        int secondIndex = towns[0] < towns[1] ?  towns[1] : towns[0];

        ArrayList<Integer> newSequence = new ArrayList<Integer>();
        for (int i = 0; i < firstIndex; i++){
            newSequence.add(individual.sequence.get(i));
        }

        ArrayList<Integer> middle = new ArrayList<Integer>();
        for (int i = secondIndex; i >= firstIndex; i--){
            middle.add(individual.sequence.get(i));
        }

        ArrayList<Integer> tail = new ArrayList<Integer>();
        for (int i = secondIndex + 1; i < individual.sequence.size(); i++){
            tail.add(individual.sequence.get(i));
        }

        newSequence.addAll(middle);
        newSequence.addAll(tail);

        return new Individual(newSequence);
    }

    private static Individual doSwapMutate(Individual individual){
        List<Integer> sequence = individual.sequence;

        int[] towns = GetUniqueTowns(individual.sequence);

        Utility.SwapInPlace(sequence, towns);

        return new Individual(sequence);
    }

    public static List<Individual> Mutate(Individual individualA, Individual individualB)
    {
        Individual newIndividualA = new Individual(individualA.sequence);
        Individual newIndividualB = new Individual(individualB.sequence);

        if (random.nextDouble() < Config.mutationChance){
            newIndividualA = doMutate(individualA);
        }

        if (random.nextDouble() < Config.mutationChance){
            newIndividualB = doMutate(individualB);
        }

        ArrayList<Individual> returnOffspring = new ArrayList<Individual>();
        returnOffspring.add(newIndividualA);
        returnOffspring.add(newIndividualB);

        return returnOffspring;
    }

    private static Individual doMutate(Individual individual){
        if (random.nextDouble() > 0.5){

            return doSwapMutate(individual);

        } else {

            return DoRotateMutate(individual);

        }
    }
}
