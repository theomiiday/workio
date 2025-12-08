package com.example.workio.data.api;

import com.example.workio.data.model.*;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);
    @POST("auth/logout")
    Call<ApiResponse<Void>> logout();

    @POST("auth/refresh-token")
    Call<LoginResponse> refreshToken(@Body RefreshTokenRequest request);

    @PUT("auth/update-email")
    Call<ApiResponse<LoginResponse.User>> updateEmail(@Body UpdateEmailRequest request);

    @POST("auth/verify-email")
    Call<ApiResponse<LoginResponse.User>> verifyEmail(@Body VerifyEmailRequest request);
    @POST("users/verify-reset-otp")
    Call<ApiResponse<Void>> verifyResetOtp(@Body VerifyResetOtpRequest request);

    @POST("auth/resend-verification")
    Call<ApiResponse<Void>> resendVerification(@Body ResendVerificationRequest request);
    @POST("users/forgot-password")
    Call<ApiResponse<Void>> forgotPassword(@Body ForgotPasswordRequest request);
    @POST("users/reset-password")
    Call<ApiResponse<Void>> resetPassword(@Body ResetPasswordRequest request);
    @PUT("users/change-password")
    Call<ApiResponse<Void>> changePassword(
            @Header("Authorization") String token,
            @Body ChangePasswordRequest request
    );





    // ==================== EMPLOYEES ====================

    @GET("employees")
    Call<ApiResponse<EmployeeListData>> getEmployees(
            @Query("page") Integer page,
            @Query("limit") Integer limit,
            @Query("role") String role
    );

    @GET("employees/{id}")
    Call<ApiResponse<Employee>> getEmployeeById(@Path("id") String id);

    @POST("employees")
    Call<ApiResponse<Employee>> createEmployee(@Body Employee employee);

    @PUT("employees/{id}")
    Call<ApiResponse<Employee>> updateEmployee(
            @Path("id") String id,
            @Body Employee employee
    );

    @DELETE("employees/{id}")
    Call<ApiResponse<Void>> deleteEmployee(@Path("id") String id);

    @GET("employees/branch/{branchId}")
    Call<ApiResponse<EmployeeListData>> getEmployeesByBranch(
            @Path("branchId") String branchId
    );

    // ==================== BRANCHES ====================

    @GET("branches")
    Call<ApiResponse<List<Branch>>> getBranches(@Header("Authorization") String token);

    @GET("branches/{id}")
    Call<ApiResponse<Branch>> getBranchById(@Path("id") String id);

    @POST("branches")
    Call<ApiResponse<Branch>> createBranch(@Body Branch branch);

    @PUT("branches/{id}")
    Call<ApiResponse<Branch>> updateBranch(@Path("id") String id, @Body Branch branch);

    @DELETE("branches/{id}")
    Call<ApiResponse<Void>> deleteBranch(@Path("id") String id);

    // ==================== SHIFTS ====================

    @GET("shifts")
    Call<ApiResponse<List<Shift>>> getShifts();

    @GET("shifts/{id}")
    Call<ApiResponse<Shift>> getShiftById(@Path("id") String id);

    @GET("shifts")
    Call<ApiResponse<List<Shift>>> getShiftsByBranch(
            @Header("Authorization") String token,
            @Query("branchId") String branchId
    );
    // ==================== SHIFT REGISTRATIONS ====================
    @GET("shift-registrations")
    Call<ApiResponse<List<ShiftRegistration>>> getAllShiftRegistrations(
            @Header("Authorization") String token
    );
    @POST("shift-registrations")
    Call<ApiResponse<ShiftRegistration>> registerShift(
            @Header("Authorization") String token,
            @Body Map<String, Object> body
    );
    @DELETE("shift-registrations/{id}")
    Call<ApiResponse<Void>> cancelShiftRegistration(
            @Header("Authorization") String token,
            @Path("id") String registrationId
    );



    // ==================== ATTENDANCE ====================

    @POST("attendance/check-in")
    Call<ApiResponse<CheckInResponse>> checkIn(
            @Header("Authorization") String token,
            @Body CheckInRequest request
    );

    @POST("attendance/check-out")
    Call<ApiResponse<CheckOutResponse>> checkOut(
            @Header("Authorization") String token,
            @Body CheckOutRequest request
    );


    @GET("attendance")
    Call<ApiResponse<List<Attendance>>> getAllAttendance(
            @Query("page") Integer page,
            @Query("limit") Integer limit
    );

    @GET("attendance/employee/{employeeId}")
    Call<ApiResponse<AttendanceListData>> getEmployeeAttendance(
            @Path("employeeId") String employeeId
    );

    @GET("attendance/employee/{employeeId}/date-range")
    Call<ApiResponse<AttendanceListData>> getAttendanceByDateRange(
            @Path("employeeId") String employeeId,
            @Query("startDate") String startDate,
            @Query("endDate") String endDate
    );

    @GET("attendance/{id}")
    Call<ApiResponse<Attendance>> getAttendanceById(@Path("id") String id);

    @PUT("attendance/{id}")
    Call<ApiResponse<Attendance>> updateAttendance(
            @Path("id") String id,
            @Body Attendance attendance
    );

    @DELETE("attendance/{id}")
    Call<ApiResponse<Void>> deleteAttendance(@Path("id") String id);

    // ==================== NOTIFICATIONS ====================

    @GET("notifications")
    Call<ApiResponse<NotificationResponse>> getNotifications(
            @Header("Authorization") String token,
            @Query("page") int page,
            @Query("limit") int limit,
            @Query("status") String status // "read", "unread", hoặc null
    );

    // 🔹 Đánh dấu 1 thông báo là đã đọc
    @PUT("notifications/{id}/read")
    Call<ApiResponse<Notification>> markAsRead(
            @Header("Authorization") String token,
            @Path("id") String notificationId
    );

    // 🔹 Đánh dấu tất cả là đã đọc
    @PUT("notifications/read-all")
    Call<ApiResponse<Object>> markAllAsRead(
            @Header("Authorization") String token
    );

    // 🔹 Xóa 1 thông báo
    @DELETE("notifications/{id}")
    Call<ApiResponse<Object>> deleteNotification(
            @Header("Authorization") String token,
            @Path("id") String notificationId
    );

    // 🔹 Xóa tất cả thông báo đã đọc
    @DELETE("notifications/read-all")
    Call<ApiResponse<Object>> deleteAllRead(
            @Header("Authorization") String token
    );
    // ==================== MESSAGES ====================

    @GET("messages/conversations")
    Call<ResponseBody> getConversations();

    @POST("messages/direct")
    Call<ApiResponse<Message>> sendDirectMessage(@Body SendMessageRequest request);

    @GET("messages/direct/{userId}")
    Call<ResponseBody> getDirectMessages(@Path("userId") String userId);
    @GET("messages/group")
    Call<ResponseBody> getGroupChatHistory();
    @POST("messages/group")
    Call<ApiResponse<Message>> sendGroupMessage(@Body SendMessageRequest request);
    @DELETE("messages/{id}")
    Call<ApiResponse<Void>> deleteMessage(@Path("id") String id);

    @PUT("messages/direct/{userId}/read")
    Call<ApiResponse<Void>> markMessagesAsRead(@Path("userId") String userId);

    // ==================== PAYROLL ====================

    @GET("payroll")
    Call<ApiResponse<PayrollListData>> getAllPayrolls();

    @GET("payroll/employee/{employeeId}")
    Call<ApiResponse<PayrollListData>> getEmployeePayrolls(@Path("employeeId") String employeeId);

    @GET("payroll/{id}")
    Call<ApiResponse<Payroll>> getPayrollById(@Path("id") String id);

    @POST("payroll")
    Call<ApiResponse<Payroll>> createPayroll(@Body Payroll payroll);

    @PUT("payroll/{id}")
    Call<ApiResponse<Payroll>> updatePayroll(@Path("id") String id, @Body Payroll payroll);

    @DELETE("payroll/{id}")
    Call<ApiResponse<Void>> deletePayroll(@Path("id") String id);

    // ==================== VIOLATIONS ====================

    @GET("violations")
    Call<ApiResponse<List<Violation>>> getAllViolations();


    @GET("violations")
    Call<ApiResponse<List<Violation>>> getEmployeeViolations(
            @Query("employeeId") String employeeId
    );




    @GET("violations/{id}")
    Call<ApiResponse<Violation>> getViolationById(@Path("id") String id);

    @POST("violations")
    Call<ApiResponse<Violation>> createViolation(@Body Violation violation);

    @PUT("violations/{id}")
    Call<ApiResponse<Violation>> updateViolation(@Path("id") String id, @Body Violation violation);

    @DELETE("violations/{id}")
    Call<ApiResponse<Void>> deleteViolation(@Path("id") String id);

    @POST("salary-goals")
    Call<ApiResponse<SalaryGoal>> createOrUpdateGoal(@Body CreateGoalRequest request);

    @GET("salary-goals/current")
    Call<ApiResponse<CurrentGoalData>> getCurrentGoal();


}
