package ru.demo.sessia5;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import ru.demo.sessia5.model.Student;
import ru.demo.sessia5.repository.StudentDao;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class StudentController implements Initializable {

    @FXML public TableColumn<Student, String> numberpp;
    @FXML public TableColumn<Student, String> specialtyNumber;
    @FXML public TableColumn<Student, String> fullName;
    @FXML public TableColumn<Student, String> faculty;
    @FXML public TableView<Student> tableView;
    @FXML private TextField searchFullNameField;
    @FXML private ComboBox<String> facultyFilterComboBox;
    @FXML private ComboBox<String> sortComboBox;

    private StudentDao studentDao = new StudentDao();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setCellTable();
        initFacultyComboBox();
        initSortComboBox();
        setupListeners();
        filterData();
    }

    private void setCellTable() {
        numberpp.setCellValueFactory(new PropertyValueFactory<>("id"));
        fullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        faculty.setCellValueFactory(new PropertyValueFactory<>("faculty"));
        specialtyNumber.setCellValueFactory(new PropertyValueFactory<>("specialtyNumber"));
    }

    private void initFacultyComboBox() {
        facultyFilterComboBox.getItems().add("Все факультеты");
        // Загрузка уникальных факультетов из БД
        List<Student> all = studentDao.findAll();
        List<String> uniqueFaculties = all.stream()
                .map(Student::getFaculty)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        facultyFilterComboBox.getItems().addAll(uniqueFaculties);
        facultyFilterComboBox.setValue("Все факультеты");
    }

    private void initSortComboBox() {
        sortComboBox.getItems().addAll("Без сортировки", "По возрастанию", "По убыванию");
        sortComboBox.setValue("Без сортировки");
    }

    private void setupListeners() {
        searchFullNameField.textProperty().addListener((obs, old, val) -> filterData());
        facultyFilterComboBox.valueProperty().addListener((obs, old, val) -> filterData());
        sortComboBox.valueProperty().addListener((obs, old, val) -> filterData());
    }

    private void filterData() {
        List<Student> students = studentDao.findAll();

        // Поиск по ФИО
        String searchText = searchFullNameField.getText();
        if (searchText != null && !searchText.isEmpty()) {
            students = students.stream()
                    .filter(s -> s.getFullName() != null && s.getFullName().toLowerCase().contains(searchText.toLowerCase()))
                    .collect(Collectors.toList());
        }

        // Фильтрация по факультету
        String selectedFaculty = facultyFilterComboBox.getValue();
        if (selectedFaculty != null && !selectedFaculty.equals("Все факультеты")) {
            students = students.stream()
                    .filter(s -> s.getFaculty().equals(selectedFaculty))
                    .collect(Collectors.toList());
        }

        // Сортировка по номеру специальности
        String sortValue = sortComboBox.getValue();
        if (sortValue != null) {
            if (sortValue.equals("По возрастанию")) {
                students.sort((s1, s2) -> Integer.compare(s1.getSpecialtyNumber(), s2.getSpecialtyNumber()));
            } else if (sortValue.equals("По убыванию")) {
                students.sort((s1, s2) -> Integer.compare(s2.getSpecialtyNumber(), s1.getSpecialtyNumber()));
            }
        }

        tableView.getItems().clear();
        tableView.getItems().addAll(students);
    }

    @FXML
    public void onAddClick(ActionEvent event) {
        showAddDialog();
        refreshFacultyComboBox();
        filterData();
    }

    @FXML
    public void onRemoveClick(ActionEvent event) {
        Student selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Ошибка", "Выберите запись для удаления", Alert.AlertType.ERROR);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение удаления");
        alert.setHeaderText("Удаление записи");
        alert.setContentText("Вы действительно хотите удалить студента \"" + selected.getFullName() + "\"?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            studentDao.delete(selected);
            refreshFacultyComboBox();
            filterData();
            showAlert("Успех", "Запись успешно удалена", Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    public void onUpdateClick(ActionEvent event) {
        Student selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Ошибка", "Выберите запись для изменения", Alert.AlertType.ERROR);
            return;
        }
        showEditDialog(selected);
        refreshFacultyComboBox();
        filterData();
    }

    private void refreshFacultyComboBox() {
        List<Student> all = studentDao.findAll();
        List<String> uniqueFaculties = all.stream()
                .map(Student::getFaculty)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        String currentValue = facultyFilterComboBox.getValue();
        facultyFilterComboBox.getItems().clear();
        facultyFilterComboBox.getItems().add("Все факультеты");
        facultyFilterComboBox.getItems().addAll(uniqueFaculties);
        if (uniqueFaculties.contains(currentValue)) {
            facultyFilterComboBox.setValue(currentValue);
        } else {
            facultyFilterComboBox.setValue("Все факультеты");
        }
    }

    private void showAddDialog() {
        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle("Добавление студента");
        dialog.setHeaderText("Введите данные студента");

        TextField fullNameField = new TextField();
        fullNameField.setPromptText("Фамилия Имя Отчество");
        TextField facultyField = new TextField();
        facultyField.setPromptText("Факультет");
        TextField specialtyNumberField = new TextField();
        specialtyNumberField.setPromptText("Номер специальности (1-500000)");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20));
        grid.add(new Label("ФИО:"), 0, 0);
        grid.add(fullNameField, 1, 0);
        grid.add(new Label("Факультет:"), 0, 1);
        grid.add(facultyField, 1, 1);
        grid.add(new Label("Номер специальности:"), 0, 2);
        grid.add(specialtyNumberField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveBtn = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == saveBtn) {
                try {
                    int specNumber = Integer.parseInt(specialtyNumberField.getText());
                    if (specNumber < 1 || specNumber > 500000) {
                        showAlert("Ошибка", "Номер специальности должен быть в диапазоне 1-500000", Alert.AlertType.ERROR);
                        return null;
                    }
                    return new Student(fullNameField.getText(), facultyField.getText(), specNumber);
                } catch (NumberFormatException e) {
                    showAlert("Ошибка", "Номер специальности должен быть числом", Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(student -> {
            studentDao.save(student);
            showAlert("Успех", "Студент успешно добавлен", Alert.AlertType.INFORMATION);
        });
    }

    private void showEditDialog(Student student) {
        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle("Редактирование студента");
        dialog.setHeaderText("Измените данные студента");

        TextField fullNameField = new TextField(student.getFullName());
        TextField facultyField = new TextField(student.getFaculty());
        TextField specialtyNumberField = new TextField(String.valueOf(student.getSpecialtyNumber()));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20));
        grid.add(new Label("ФИО:"), 0, 0);
        grid.add(fullNameField, 1, 0);
        grid.add(new Label("Факультет:"), 0, 1);
        grid.add(facultyField, 1, 1);
        grid.add(new Label("Номер специальности:"), 0, 2);
        grid.add(specialtyNumberField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveBtn = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == saveBtn) {
                try {
                    int specNumber = Integer.parseInt(specialtyNumberField.getText());
                    if (specNumber < 1 || specNumber > 500000) {
                        showAlert("Ошибка", "Номер специальности должен быть в диапазоне 1-500000", Alert.AlertType.ERROR);
                        return null;
                    }
                    student.setFullName(fullNameField.getText());
                    student.setFaculty(facultyField.getText());
                    student.setSpecialtyNumber(specNumber);
                    return student;
                } catch (NumberFormatException e) {
                    showAlert("Ошибка", "Номер специальности должен быть числом", Alert.AlertType.ERROR);
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updated -> {
            studentDao.update(updated);
            showAlert("Успех", "Студент успешно обновлен", Alert.AlertType.INFORMATION);
        });
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}