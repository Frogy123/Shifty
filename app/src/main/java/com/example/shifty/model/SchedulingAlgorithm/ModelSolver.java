package com.example.shifty.model.SchedulingAlgorithm;
import com.example.shifty.model.Employee;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.BoolVar;

import java.util.List;


public class ModelSolver {

    Model model;

    //constants:
    final int MAX_DAYS = 7;
    final int MAX_HOURS = 22;
    final int  SHIFT_LENGTH = 8; //hours


    int EmployeeCount;
    Employee[] employeeMap;


    boolean[][][] availability = new boolean[EmployeeCount][MAX_DAYS][MAX_HOURS];

    int[][] systemNeeds = new int[MAX_DAYS][MAX_HOURS]; //number of employees needed for each hour
    //int[][] propertiesSystemNeeds = new int[MAX_DAYS][MAX_HOURS]; //number of sepcial employees needed for each hour

    BoolVar[][][] schedule = new BoolVar[EmployeeCount][MAX_DAYS][MAX_HOURS];

    //choco model:
    //int[][][]

    public ModelSolver(List<Employee> _employees){

        model = new Model("Scheduling Algorithm");

        //initialize the model
        EmployeeCount = _employees.size();
        employeeMap = new Employee[EmployeeCount];
        for(int i = 0; i < _employees.size(); i++){
            employeeMap[i] = _employees.get(i);
        }



        initializeAvailability();
        initializeSchedule();
        initializeConstraints();
        //TODO initialize system needs, need to get it from admin.


    }

    private void initializeConstraints() {

        initializeConstraintAvailability();
        initializeConstraintsSystemNeeds();
        initializeConstraintWorkHours();

    }

    private void initializeConstraintAvailability(){
        for(int i=0; i<EmployeeCount; i++){
            for(int j=0; j<MAX_DAYS; j++){
                for(int k=0; k<MAX_HOURS; k++){
                    if(!availability[i][j][k]){
                        model.arithm(schedule[i][j][k], "=", 0).post();
                    }
                }
            }
        }
    }
    
    private void initializeConstraintsSystemNeeds(){
        for(int j=0; j<MAX_DAYS; j++){
            for(int k=0; k<MAX_HOURS; k++){
                BoolVar[] employeesScheduled = new BoolVar[EmployeeCount];
                for (int i = 0; i < EmployeeCount; i++) {
                    employeesScheduled[i] = schedule[i][j][k];
                }
                // Sum constraint: total employees scheduled equals system needs
                model.sum(employeesScheduled, "=", systemNeeds[j][k]).post();
            }
        }
    }

    private void initializeConstraintWorkHours() {
        for (int i = 0; i < EmployeeCount; i++) {
            for (int j = 0; j < MAX_DAYS; j++) {
                for (int k = 0; k < MAX_HOURS; k++) {
                    // Check if the employee has worked in the last 8 hours
                    if (k >= 8) {
                        for (int h = k - 8; h < k; h++) {
                            model.ifThen(
                                    model.arithm(schedule[i][j][h], "=", 1),
                                    model.arithm(schedule[i][j][k], "=", 0)
                            );
                        }
                    }

                    // Check if the employee has already worked that day
                    for (int h = 0; h < k; h++) {
                        model.ifThen(
                                model.arithm(schedule[i][j][h], "=", 1),
                                model.arithm(schedule[i][j][k], "=", 0)
                        );
                    }
                }
                int lastDay = ((j - 1) + MAX_DAYS) % MAX_DAYS;

                //taking care of 8 hours of rest between days :)
                for(int l = 0; i< 7; i++){
                            model.ifThen(model.arithm(
                            schedule[i][lastDay][15 + l], "=", 1),
                            model.arithm(schedule[i][j][l], "=", 0));
                }
            }
        }
    }

    private void initializeSchedule() {
        for(int i=0; i<EmployeeCount; i++){
            for(int j=0; j<MAX_DAYS; j++){
                for(int k=0; k<MAX_HOURS; k++){
                    schedule[i][j][k] = model.boolVar("schedule_" + i + "_" + j + "_" + k);
                }
            }
        }
    }

    private void initializeAvailability() {
        for(int i=0; i<EmployeeCount; i++){
            for(int j=0; j<MAX_DAYS; j++){
                for(int k=0; k<MAX_HOURS; k++){
                    if(employeeMap[i].isAvailable(j, k)){
                        availability[i][j][k] = true;
                    }else{
                        availability[i][j][k] = false;
                    }
                }
            }
        }
    }



    public Schedule Solve(){
        if(this.model.getSolver().solve()){

            Schedule s = new Schedule();



            for(int i=0; i<EmployeeCount; i++){
                for(int j=0; j<MAX_DAYS; j++){
                    for(int k=0; k<MAX_HOURS; k++){
                        if(schedule[i][j][k].getValue() == 1){
                            s.addEmployeeHour(employeeMap[i], j, k);
                        }
                    }
                }
            }
        }

        return null;

    }

    //variables:
}
