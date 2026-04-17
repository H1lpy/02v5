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
    @FXML public TableColumn<Student, String> lastName;
    @FXML public TableColumn<Student, String> firstName;
    @FXML public TableColumn<Student, String> middleName;
    @FXML public TableColumn<Student, String> faculty;
    @FXML public TableView<Student> tableView;
    @FXML private TextField searchFioField;
    @FXML private ComboBox<String> facultyFilterComboBox;
    @FXML private ComboBox<String> sortComboBox;

    private StudentDao studentDao = new StudentDao();
    private ObservableList<String> facultyList = FXCollections.observableArrayList();

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
        specialtyNumber.setCellValueFactory(new PropertyValueFactory<>("specialtyNumber"));
        lastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        firstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        middleName.setCellValueFactory(new PropertyValueFactory<>("middleName"));
        faculty.setCellValueFactory(new PropertyValueFactory<>("faculty"));
    }

    private void initFacultyComboBox() {
        facultyFilterComboBox.getItems().add("Все факультеты");
        List<Student> allStudents = studentDao.findAll();
        facultyList.addAll(allStudents.stream()
                .map(Student::getFaculty)
                .distinct()
                .sorted()
                .collect(Collectors.toList()));
        facultyFilterComboBox.getItems().addAll(facultyList);
        facultyFilterComboBox.setValue("Все факультеты");
    }

    private void initSortComboBox() {
        sortComboBox.getItems().addAll("Без сортировки", "По возрастанию", "По убыванию");
        sortComboBox.setValue("Без сортировки");
    }

    private void setupListeners() {
        searchFioField.textProperty().addListener((obs, old, val) -> filterData());
        facultyFilterComboBox.valueProperty().addListener((obs, old, val) -> filterData());
        sortComboBox.valueProperty().addListener((obs, old, val) -> filterData());
    }

    private void filterData() {
        List<Student> students = studentDao.findAll();

        String searchText = searchFioField.getText();
        if (searchText != null && !searchText.isEmpty()) {
            students = students.stream()
                    .filter(s -> s.getFullName() != null &&
                            s.getFullName().toLowerCase().contains(searchText.toLowerCase()))
                    .collect(Collectors.toList());
        }

        String selectedFaculty = facultyFilterComboBox.getValue();
        if (selectedFaculty != null && !selectedFaculty.equals("Все факультеты")) {
            students = students.stream()
                    .filter(s -> s.getFaculty() != null && s.getFaculty().equals(selectedFaculty))
                    .collect(Collectors.toList());
        }

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

    private void refreshFacultyList() {
        List<Student> allStudents = studentDao.findAll();
        facultyList.clear();
        facultyList.addAll(allStudents.stream()
                .map(Student::getFaculty)
                .distinct()
                .sorted()
                .collect(Collectors.toList()));
        String currentValue = facultyFilterComboBox.getValue();
        facultyFilterComboBox.getItems().clear();
        facultyFilterComboBox.getItems().add("Все факультеты");
        facultyFilterComboBox.getItems().addAll(facultyList);
        if (facultyList.contains(currentValue)) {
            facultyFilterComboBox.setValue(currentValue);
        } else {
            facultyFilterComboBox.setValue("Все факультеты");
        }
    }

    @FXML
    public void onAddClick(ActionEvent event) {
        showAddDialog();
        refreshFacultyList();
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
            refreshFacultyList();
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
        refreshFacultyList();
        filterData();
    }

    private void showAddDialog() {
        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle("Добавление студента");
        dialog.setHeaderText("Введите данные студента");

        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Фамилия");
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("Имя");
        TextField middleNameField = new TextField();
        middleNameField.setPromptText("Отчество");
        ComboBox<String> facultyCombo = new ComboBox<>();
        facultyCombo.setPromptText("Факультет");
        facultyCombo.setEditable(true);
        facultyCombo.getItems().addAll(facultyList);
        TextField specialtyNumberField = new TextField();
        specialtyNumberField.setPromptText("Номер специальности");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20));
        grid.add(new Label("Фамилия:"), 0, 0);
        grid.add(lastNameField, 1, 0);
        grid.add(new Label("Имя:"), 0, 1);
        grid.add(firstNameField, 1, 1);
        grid.add(new Label("Отчество:"), 0, 2);
        grid.add(middleNameField, 1, 2);
        grid.add(new Label("Факультет:"), 0, 3);
        grid.add(facultyCombo, 1, 3);
        grid.add(new Label("Номер специальности:"), 0, 4);
        grid.add(specialtyNumberField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveBtn = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == saveBtn) {
                try {
                    int specialtyNumber = Integer.parseInt(specialtyNumberField.getText());
                    if (specialtyNumber < 1 || specialtyNumber > 500000) {
                        showAlert("Ошибка", "Номер специальности должен быть в диапазоне 1-500000", Alert.AlertType.ERROR);
                        return null;
                    }
                    String faculty = facultyCombo.getValue();
                    if (faculty == null || faculty.trim().isEmpty()) {
                        showAlert("Ошибка", "Введите факультет", Alert.AlertType.ERROR);
                        return null;
                    }
                    return new Student(faculty, specialtyNumber, lastNameField.getText(),
                            firstNameField.getText(), middleNameField.getText());
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

        TextField lastNameField = new TextField(student.getLastName());
        TextField firstNameField = new TextField(student.getFirstName());
        TextField middleNameField = new TextField(student.getMiddleName());
        ComboBox<String> facultyCombo = new ComboBox<>();
        facultyCombo.setEditable(true);
        facultyCombo.getItems().addAll(facultyList);
        facultyCombo.setValue(student.getFaculty());
        TextField specialtyNumberField = new TextField(String.valueOf(student.getSpecialtyNumber()));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20));
        grid.add(new Label("Фамилия:"), 0, 0);
        grid.add(lastNameField, 1, 0);
        grid.add(new Label("Имя:"), 0, 1);
        grid.add(firstNameField, 1, 1);
        grid.add(new Label("Отчество:"), 0, 2);
        grid.add(middleNameField, 1, 2);
        grid.add(new Label("Факультет:"), 0, 3);
        grid.add(facultyCombo, 1, 3);
        grid.add(new Label("Номер специальности:"), 0, 4);
        grid.add(specialtyNumberField, 1, 4);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveBtn = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        dialog.setResultConverter(button -> {
            if (button == saveBtn) {
                try {
                    int specialtyNumber = Integer.parseInt(specialtyNumberField.getText());
                    if (specialtyNumber < 1 || specialtyNumber > 500000) {
                        showAlert("Ошибка", "Номер специальности должен быть в диапазоне 1-500000", Alert.AlertType.ERROR);
                        return null;
                    }
                    String faculty = facultyCombo.getValue();
                    if (faculty == null || faculty.trim().isEmpty()) {
                        showAlert("Ошибка", "Введите факультет", Alert.AlertType.ERROR);
                        return null;
                    }
                    student.setLastName(lastNameField.getText());
                    student.setFirstName(firstNameField.getText());
                    student.setMiddleName(middleNameField.getText());
                    student.setFaculty(faculty);
                    student.setSpecialtyNumber(specialtyNumber);
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