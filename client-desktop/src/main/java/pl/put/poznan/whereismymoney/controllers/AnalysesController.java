package pl.put.poznan.whereismymoney.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import pl.put.poznan.whereismymoney.model.Budget;
import pl.put.poznan.whereismymoney.model.Category;
import pl.put.poznan.whereismymoney.service.AnalysesService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AnalysesController implements Controller {
    @FXML
    private PieChart outcomeChart;
    @FXML
    private Label recentIncome, overallOutcome, categoryOutcome, categoryStake, categoryLimit, limitStake;
    @FXML
    private ComboBox<Budget> budgetPicker;
    @FXML
    private ComboBox<Category> categoryPicker;
    @FXML
    private DatePicker fromPicker, toPicker;

    private AnalysesService analysesService;

    @Inject
    public AnalysesController(AnalysesService analysesService) {
        this.analysesService = analysesService;
    }

    @Override
    public void clear() {
        budgetPicker.getSelectionModel().clearSelection();
        categoryPicker.getSelectionModel().clearSelection();

        budgetPicker.setItems(null);
        categoryPicker.setItems(null);
        fromPicker.setValue(null);
        toPicker.setValue(null);

        recentIncome.setText("-");
        categoryOutcome.setText("-");
        categoryStake.setText("-");
        categoryLimit.setText("-");
        limitStake.setText("-");

        outcomeChart.setData(FXCollections.observableArrayList());
    }

    @Override
    public void refresh() {
        initialize();
    }

    @FXML
    private void initialize() {
        List<Budget> budgets = analysesService.getBudgets();
        budgetPicker.setItems(FXCollections.observableArrayList(budgets));
    }

    @FXML
    private void onBudgetSelection() {
        categoryPicker.getSelectionModel().clearSelection();
        fromPicker.setValue(null);
        toPicker.setValue(null);
        Budget selected = budgetPicker.getSelectionModel().getSelectedItem();
        if (selected != null) {
            categoryPicker.setItems(FXCollections.observableArrayList(analysesService.getCategories(selected)));
        }
    }

    @FXML
    private void onCategorySelection() {
        fillWithData();
    }

    @FXML
    private void onPeriodSelection() {
        fillWithData();
    }

    private void fillWithData() {
        if (isPeriodSelected() && isCategorySelected()) {
            Category selected = categoryPicker.getSelectionModel().getSelectedItem();
            LocalDate from = fromPicker.getValue();
            LocalDate to = toPicker.getValue();
            BigDecimal income = analysesService.getIncome(categoryPicker.getItems(), from, to);
            BigDecimal outcome = analysesService.getOutcome(selected, from, to);
            BigDecimal generalOutcome = analysesService.getOverallOutcome(categoryPicker.getItems(), from, to);

            recentIncome.setText(income.toPlainString() + " PLN");
            overallOutcome.setText(generalOutcome.toPlainString() + " PLN");
            categoryOutcome.setText(outcome.toPlainString() + " PLN");
            categoryStake.setText(analysesService.getStake(generalOutcome, outcome).toPlainString() + "%");
            categoryLimit.setText(selected.getLimit().toPlainString() + " PLN");
            limitStake.setText(analysesService.getStake(selected.getLimit(), outcome).toPlainString() + "%");
            outcomeChart.setData(FXCollections.observableArrayList(getChartData(generalOutcome, from, to)));
        }
    }

    private boolean isPeriodSelected() {
        return !(fromPicker.getValue() == null || toPicker.getValue() == null);
    }

    private boolean isCategorySelected() {
        return categoryPicker.getSelectionModel().getSelectedItem() != null;
    }

    private ArrayList<PieChart.Data> getChartData(BigDecimal generalOutcome, LocalDate from, LocalDate to) {
        ArrayList<PieChart.Data> chartData = new ArrayList<>(categoryPicker.getItems().size());
        for (Category category : categoryPicker.getItems()) {
            BigDecimal outcome = analysesService.getOutcome(category, from, to);
            chartData.add(new PieChart.Data(category.getName(),
                    analysesService.getStake(generalOutcome, outcome)
                            .doubleValue()));
        }
        return chartData;
    }
}
