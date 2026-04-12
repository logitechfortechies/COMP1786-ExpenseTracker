package com.example.expensetracker.network;

import com.example.expensetracker.models.Expense;
import com.example.expensetracker.models.Project;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/** Retrofit interface — all HTTP endpoints on the Node.js server. */
public interface ApiService {

    @POST("projects")
    Call<Project> uploadProject(@Body Project project);

    @PUT("projects/{id}")
    Call<Project> updateProject(@Path("id") int id, @Body Project project);

    @POST("expenses")
    Call<Expense> uploadExpense(@Body Expense expense);

    @GET("projects")
    Call<List<Project>> getProjects();

    @GET("projects/{id}/expenses")
    Call<List<Expense>> getExpenses(@Path("id") int projectId);
}