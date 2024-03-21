package ru.wwerlosh.task;


import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

public class DocGenerator {

    private XWPFDocument doc;

    public DocGenerator() {
        doc = new XWPFDocument();
    }

    public void write(String fileName) {
        try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
            doc.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            doc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void makeTitle(String name, String group, String option) {
        XWPFParagraph title = doc.createParagraph();
        title.setAlignment(ParagraphAlignment.RIGHT);
        XWPFRun titleRun = title.createRun();

        titleRun.setFontFamily("Times New Roman");
        titleRun.setFontSize(14);
        titleRun.setText("Работу выполнил: " + name);
        titleRun.addBreak();
        titleRun.setText("Группа: " + group);
        titleRun.addBreak();
        titleRun.setText("Вариант: " + option);
        titleRun.addBreak();
    }

    public void makeInputDataParagraph(ArrayList<Double> selection) {
        XWPFParagraph startDataPar = doc.createParagraph();
        XWPFRun startDataRun = startDataPar.createRun();

        startDataRun.setFontSize(14);
        startDataRun.setFontFamily("Times New Roman");
        startDataRun.setText("0. Входные данные");

        int numRows = (int) Math.ceil((double) selection.size() / 10);
        int numCols = Math.min(10, selection.size());

        XWPFTable startDataTable = doc.createTable(numRows, numCols);

        for (int i = 0; i < numRows; i++) {
            XWPFTableRow row = startDataTable.getRow(i);

            for (int j = 0; j < numCols; j++) {
                int index = i * 10 + j;
                if (index < selection.size()) {
                    XWPFTableCell cell = row.getCell(j);
                    cell.setText(selection.get(index).toString());
                }
            }
        }
    }

    public void makeSizeParagraph(int size) {
        XWPFParagraph sizePar = doc.createParagraph();
        XWPFRun sizeRun = sizePar.createRun();

        sizeRun.setFontSize(14);
        sizeRun.setFontFamily("Times New Roman");
        sizeRun.setText("1. Объем выборки: " + size);
        sizeRun.addBreak();
    }

    public void makeMinValueParagraph(double minValue) {
        XWPFParagraph minValuePar = doc.createParagraph();
        XWPFRun minValueRun = minValuePar.createRun();

        minValueRun.setFontSize(14);
        minValueRun.setFontFamily("Times New Roman");
        minValueRun.setText("2. Минимальное значение: " + minValue);
        minValueRun.addBreak();
    }

    public void makeMaxValueParagraph(double maxValue) {
        XWPFParagraph maxValuePar = doc.createParagraph();
        XWPFRun maxValueRun = maxValuePar.createRun();

        maxValueRun.setFontSize(14);
        maxValueRun.setFontFamily("Times New Roman");
        maxValueRun.setText("3. Максимальное значение: " + maxValue);
        maxValueRun.addBreak();
    }

    public void makeRangeParagraph(double range) {
        XWPFParagraph rangePar = doc.createParagraph();
        XWPFRun rangeRun = rangePar.createRun();

        rangeRun.setFontSize(14);
        rangeRun.setFontFamily("Times New Roman");
        rangeRun.setText("4. Размах: " + range);
        rangeRun.addBreak();
    }

    public void makeVariationSeriesParagraph(double partIntervalCount, double partIntervalStep, List<List<List<Double>>> variationArrayOutput, List<List<Double>> variationArray) {
        XWPFParagraph variationSeriesPar = doc.createParagraph();
        XWPFRun variationSeriesRun = variationSeriesPar.createRun();

        variationSeriesRun.setFontSize(14);
        variationSeriesRun.setFontFamily("Times New Roman");
        variationSeriesRun.setText("5. Интервальный вариационный ряд");
        variationSeriesRun.addBreak();
        variationSeriesRun.setText("Число интервалов = " + partIntervalCount);
        variationSeriesRun.addBreak();
        variationSeriesRun.setText("Длина интервала = " + partIntervalStep);
        variationSeriesRun.addBreak();
        variationSeriesRun.setText("Вариационный ряд:");

        int size1 = variationArrayOutput.get(0).size();
        int size2 = variationArrayOutput.size();

        XWPFTable variationSeriesTable = doc.createTable(size1, size2);

        for (int row = 0; row < size1; row++) {
            for (int col = 0; col < size2; col++) {
                XWPFTableCell cell = variationSeriesTable.getRow(row).getCell(col);
                if (row == 0) {
                    cell.setText(variationArrayOutput.get(col).get(row).toString());
                } else {
                    cell.setText(variationArray.get(col).get(row).toString());
                }
            }
        }
    }

    public void makeRelationsFrequencyParagraph(List<List<List<Double>>> variationArrayOutput, List<List<Double>> relativeFrequencies) {
        XWPFParagraph relationsFrequencyPar = doc.createParagraph();
        XWPFRun relationsFrequencyRun = relationsFrequencyPar.createRun();

        relationsFrequencyRun.setFontSize(14);
        relationsFrequencyRun.setFontFamily("Times New Roman");
        relationsFrequencyRun.setText("Относительные частоты");
        relationsFrequencyRun.addBreak();

        int size1 = variationArrayOutput.get(0).size();
        int size2 = variationArrayOutput.size();

        XWPFTable relationsFrequencyTable = doc.createTable(size1, size2);

        for (int row = 0; row < variationArrayOutput.get(0).size(); row++) {
            for (int col = 0; col < variationArrayOutput.size(); col++) {
                XWPFTableCell cell = relationsFrequencyTable.getRow(row).getCell(col);
                if (row == 0) {
                    cell.setText(variationArrayOutput.get(col).get(row).toString());
                } else {
                    cell.setText(relativeFrequencies.get(col).get(row).toString());
                }
            }
        }
    }

    public void makePointEstimatesParagraph(double averageValue, double variance, double fixedVariance, double mediana, double moda) {
        XWPFParagraph pointEstimatesPar = doc.createParagraph();
        XWPFRun pointEstimatesRun = pointEstimatesPar.createRun();

        pointEstimatesRun.setFontSize(14);
        pointEstimatesRun.setFontFamily("Times New Roman");
        pointEstimatesRun.setText("6. Точечные оценки параметров распределения");
        pointEstimatesRun.addBreak();
        pointEstimatesRun.setText("Выборочное среднее: " + averageValue);
        pointEstimatesRun.addBreak();
        pointEstimatesRun.setText("Дисперсия: " + variance);
        pointEstimatesRun.addBreak();
        pointEstimatesRun.setText("Исправленная дисперсия: " + fixedVariance);
        pointEstimatesRun.addBreak();
        pointEstimatesRun.setText("Медиана: " + mediana);
        pointEstimatesRun.addBreak();
        pointEstimatesRun.setText("Мода: " + moda);
        pointEstimatesRun.addBreak();
    }

    public void makeIntervalEstimationParagraph(double leftSideAverage, double rightSideAverage, double leftSideVariance, double rightSideVariance) {
        XWPFParagraph intervalEstimationPar = doc.createParagraph();
        XWPFRun intervalEstimationRun = intervalEstimationPar.createRun();

        intervalEstimationRun.setFontSize(14);
        intervalEstimationRun.setFontFamily("Times New Roman");
        intervalEstimationRun.setText("7. Доверительный интервал для мат.ожидания = (" + leftSideAverage + " ; " + rightSideAverage + ")");
        intervalEstimationRun.addBreak();
        intervalEstimationRun.setText("Доверительный интервал для среднего квадратичного отклонения = (" + leftSideVariance + " ; " + rightSideVariance + ")");
        intervalEstimationRun.addBreak();
    }

    public void makeHypothesisParagraph(boolean b) {
        XWPFParagraph hypothesisPar = doc.createParagraph();
        XWPFRun hypothesisRun = hypothesisPar.createRun();

        hypothesisRun.setFontSize(14);
        hypothesisRun.setFontFamily("Times New Roman");
        hypothesisRun.setText("8. Проверяем гипотезу о нормальном распределении");
        hypothesisRun.addBreak();

        if (b) {
            hypothesisRun.setText("Нет оснований отвергнуть нулевую гипотезу, " +
                    "генеральная совокупность из которой сделана выборка, " +
                    "распределена по нормальному закону");
        } else {
            hypothesisRun.setText("Распределение ген совокупности не является нормальным");
        }
        hypothesisRun.addBreak();
    }
}
