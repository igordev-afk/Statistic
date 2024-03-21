package ru.wwerlosh.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import ru.wwerlosh.task.io.StatisticsIOManager;

public class SelectionProcessor {

    private static int size;
    private static double minValue;
    private static double maxValue;
    private static long partIntervalCount;
    private static double partIntervalStep;
    private static double range;
    private static double averageValue;
    private static double variance;
    private static double fixedVariance;
    private static double mediana;
    private static double moda;

    private static ArrayList<Double> selection;
    private static HashMap<Double, Integer> frequencies;
    private static final List<List<Double>> relativeFrequencies = new ArrayList<>();
    private static final List<List<Double>> variationArray = new ArrayList<>();
    private static final List<List<List<Double>>> variationArrayOutput = new ArrayList<>();

    private static final DocGenerator docGenerator = new DocGenerator();
    private static StatisticsIOManager ioManager;

    public static void process(String... args) {
        getSelection(args);

        calculateSize();
        calculateMinValue();
        calculateMaxValue();
        calculateRange();
        calculateVariationSeries();
        calculatePointEstimatesOfDistributionParameters();
        calculateIntervalEstimation();

        checkHypothesis();
    }

    public static void getSelection(String... args) {
        ioManager = new StatisticsIOManager(args);
        ioManager.read(args[2]);
        selection = ioManager.getSelection();
        docGenerator.makeTitle(args[1], args[0], args[2]);
        docGenerator.makeInputDataParagraph(selection);
    }

    private static void calculateSize() {
        size = selection.size();
        docGenerator.makeSizeParagraph(size);
    }

    private static void calculateMinValue() {
        minValue = selection.stream().min(Double::compareTo).orElse(0.0);
        docGenerator.makeMinValueParagraph(minValue);
    }

    private static void calculateMaxValue() {
        maxValue = selection.stream().max(Double::compareTo).orElse(0.0);
        docGenerator.makeMaxValueParagraph(maxValue);
    }

    private static void calculateRange() {
        range = (double) Math.round((maxValue - minValue) * 100) / 100;
        docGenerator.makeRangeParagraph(range);
    }

    private static void calculateVariationSeries() {
        calculateFrequencies();
        calculatePartIntervalCount();
        calculatePartIntervalStep();

        double currentIntervalStart = minValue;
        double currentIntervalEnd = 0;
        int freqSum = 0;
        int cnt = 1;

        Set<Double> uniqueValues = new TreeSet<>(selection);
        for (Double value : uniqueValues) {
            currentIntervalEnd = roundToTwoDecimals(currentIntervalStart + partIntervalStep);
            if (value >= currentIntervalStart && value < currentIntervalEnd || cnt == partIntervalCount) {
                freqSum += frequencies.get(value);
            } else {
                addIntervalData(currentIntervalStart, currentIntervalEnd, freqSum);
                currentIntervalStart = currentIntervalEnd;
                freqSum = frequencies.get(value);
                cnt++;
            }
        }

        addIntervalData(currentIntervalStart, currentIntervalEnd, freqSum);

        docGenerator.makeVariationSeriesParagraph(partIntervalCount, partIntervalStep, variationArrayOutput, variationArray);
        docGenerator.makeRelationsFrequencyParagraph(variationArrayOutput, relativeFrequencies);
    }

    private static void calculateFrequencies() {
        frequencies = new HashMap<>();
        for (double element : selection) {
            frequencies.put(element, frequencies.getOrDefault(element, 0) + 1);
        }
    }

    private static void calculatePartIntervalCount() {
        partIntervalCount = Math.round(1 + 3.322 * Math.log10(size));
    }

    private static void calculatePartIntervalStep() {
        partIntervalStep = Math.ceil((maxValue - minValue) * 100 / partIntervalCount) / 100;
    }

    private static double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private static void addIntervalData(double start, double end, int freqSum) {
        double mid = roundToTwoDecimals((start + end) / 2);
        double relativeFrequency = roundToTwoDecimals((double) freqSum / size * 100);

        variationArray.add(Arrays.asList(mid, (double) freqSum));
        relativeFrequencies.add(Arrays.asList(mid, relativeFrequency));
        variationArrayOutput.add(Arrays.asList(Arrays.asList(start, end), Collections.singletonList(relativeFrequency)));
    }

    private static void calculatePointEstimatesOfDistributionParameters() {
        calculateAverageValue();
        calculateVariance();
        calculateFixedVariance();
        calculateMediana();
        calculateModa();

        docGenerator.makePointEstimatesParagraph(averageValue, variance, fixedVariance, mediana, moda);
    }

    private static void calculateAverageValue() {
        double sum = 0;
        for (List<Double> doubles : variationArray) {
            sum += doubles.get(0) * doubles.get(1);
        }
        averageValue = (double) Math.round(sum / size * 100) / 100;
    }

    private static void calculateVariance() {
        double tmp = 0;
        for (List<Double> doubles : variationArray) {
            tmp += Math.pow((doubles.get(0) - averageValue), 2) * doubles.get(1);
        }
        variance = (double) Math.round(tmp / size * 100) / 100;
    }

    private static void calculateFixedVariance() {
        fixedVariance = (double) Math.round(variance * ((double) size / (size - 1)) * 100) / 100;
    }

    private static void calculateMediana() {
        int accumulatedFrequencies = 0, i = 0;
        while (accumulatedFrequencies <= size / 2) {
            accumulatedFrequencies += variationArray.get(i).get(1);
            i++;
        }

        mediana = variationArray.get(i - 1).get(0);
    }

    private static void calculateModa() {
        moda = Collections.max(frequencies.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    private static void calculateIntervalEstimation() {
        double t = 1.655, q = 0.143;
        double leftSideAverage = (double) Math.round((averageValue - t * (Math.sqrt(fixedVariance) / Math.sqrt(size))) * 100) / 100;
        double rightSideAverage = (double) Math.round((averageValue + t * (Math.sqrt(fixedVariance) / Math.sqrt(size))) * 100) / 100;
        double leftSideVariance = (double) Math.round(Math.sqrt(fixedVariance) * (1 - q) * 100) / 100;
        double rightSideVariance = (double) Math.round(Math.sqrt(fixedVariance) * (1 + q) * 100) / 100;

        docGenerator.makeIntervalEstimationParagraph(leftSideAverage, rightSideAverage, leftSideVariance, rightSideVariance);
    }

    private static void checkHypothesis() {
        double Xview = 0, Xcritical = 11.07;
        for (List<Double> relativeFrequency : variationArray) {
            double u = (relativeFrequency.get(0) - averageValue) / Math.sqrt(fixedVariance);
            double p = partIntervalStep / Math.sqrt(fixedVariance) * gaussFunc(u);
            double ni = size * p;
            Xview += Math.pow((relativeFrequency.get(1) - ni), 2) / ni;
        }

        docGenerator.makeHypothesisParagraph(Xview < Xcritical);
        ioManager.write(docGenerator);
    }

    private static double gaussFunc(double u) {
        return ((1 / Math.sqrt(2 * Math.PI)) * Math.exp(-1 * (Math.pow(u, 2) / 2)));
    }
}
