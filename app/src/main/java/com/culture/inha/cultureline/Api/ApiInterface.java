package com.culture.inha.cultureline.Api;

import com.culture.inha.cultureline.DataSet.Answer;
import com.culture.inha.cultureline.DataSet.Categories;
import com.culture.inha.cultureline.DataSet.Comment;
import com.culture.inha.cultureline.DataSet.Question;
import com.culture.inha.cultureline.DataSet.QuestionSet;
import com.culture.inha.cultureline.DataSet.LoginResult;
import com.culture.inha.cultureline.DataSet.User;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Jaeseok on 2018-06-24.
 */

public interface ApiInterface {

    public static String baseUrl = "http://198.13.50.135/api/";

    @Headers({
            "Accept:application/json",
            "Content-Type:application/x-www-form-urlencoded"})
    @POST("social/login/google")
    Call<LoginResult> registGoogle(@Query("email") String email, @Query("token") String token);

    @Headers({
            "Accept:application/json",
            "Content-Type:application/x-www-form-urlencoded"})
    @POST("auth/register")
    Call<LoginResult> register(@Query("name") String name,
                               @Query("stu_id") String stu_id,
                               @Query("major") String major,
                               @Query("email") String email,
                               @Query("password") String password,
                               @Query("password_confirmation") String password_confirmation);

    @Headers({
            "Accept:application/json",
            "Content-Type:application/x-www-form-urlencoded"})
    @POST("social/register")
    Call<User> registerSns(@Header("Authorization") String token,
                           @Query("name") String name,
                           @Query("stu_id") String stu_id,
                           @Query("major") String major);

    @Headers({
            "Accept:application/json",
            "Content-Type:application/x-www-form-urlencoded"})
    @POST("auth/login")
    Call<LoginResult> login(@Query("email") String email, @Query("password") String password);

    @Headers({
            "Accept:application/json",
            "Content-Type:application/x-www-form-urlencoded"})
    @GET("auth/me")
    Call<User> myProfile(@Header("Authorization") String token);

    @Headers({
            "Accept:application/json",
            "Content-Type:application/x-www-form-urlencoded"})
    @GET("category")
    Call<ArrayList<Categories>> category(@Header("Authorization") String token);

    @Headers({
            "Accept:application/json",
            "Content-Type:application/x-www-form-urlencoded"})
    @GET("qna")
    Call<QuestionSet> mainboard(@Header("Authorization") String token);

    @Headers({
            "Accept:application/json",
            "Content-Type:application/x-www-form-urlencoded"})
    @GET("qna")
    Call<QuestionSet> mainBoardNext(@Header("Authorization") String token,
                                    @Query("page") int pageNum);

    @Headers({
            "Accept:application/json",
            "Content-Type:application/x-www-form-urlencoded"})
    @GET("category/search/{category_id}")
    Call<QuestionSet> mainBoardCategory(@Header("Authorization") String token,
                                        @Path("category_id") String categoryId);

    @Headers({
            "Accept:application/json",
            "Content-Type:application/x-www-form-urlencoded"})
    @GET("auth/question")
    Call<ArrayList<Question>> myQuestion(@Header("Authorization") String token);


    /**
     * 질문관련
     */
    @Headers({
            "Accept:application/json",
            "Content-Type:application/x-www-form-urlencoded"})
    @POST("qna")
    Call<Question> postQuestion(@Header("Authorization") String token,
                                @Query("categories") String category,
                                @Query("title") String title,
                                @Query("contents") String contents);

    @Headers({
            "Accept:application/json",
            "Content-Type:application/x-www-form-urlencoded"})
    @PUT("qna/{qna}")
    Call<Question> updateQuestion(@Header("Authorization") String token,
                                  @Path("qna") int questionId,
                                  @Query("categories") String category,
                                  @Query("title") String title,
                                  @Query("contents") String contents);

    @Headers({
            "Accept:application/json",
            "Content-Type:application/x-www-form-urlencoded"})
    @DELETE("qna/{qna}")
    Call<String> deleteQuestion(@Header("Authorization") String token,
                                @Path("qna") int questionId);


    /**
     * 답변관련
     */
    @Headers({
            "Accept:application/json",
            "Content-Type:application/x-www-form-urlencoded"})
    @POST("question/{question}/answer")
    Call<Answer> postAnswer(@Header("Authorization") String token,
                            @Path("question") int questionId,
                            @Query("contents") String contents);

    @Headers({
            "Accept:application/json",
            "Content-Type:application/x-www-form-urlencoded"})
    @PUT("question/{question}/answer/{answer}")
    Call<String> updateAnswer(@Header("Authorization") String token,
                              @Path("question") int questionId,
                              @Path("answer") int answerId,
                              @Query("contents") String contents);

    @Headers({
            "Accept:application/json",
            "Content-Type:application/x-www-form-urlencoded"})
    @DELETE("question/{question}/answer/{answer}")
    Call<String> deleteAnswer(@Header("Authorization") String token,
                              @Path("question") int questionId,
                              @Path("answer") int answerId);

    @Headers({
            "Accept:application/json",
            "Content-Type:application/x-www-form-urlencoded"})
    @POST("question/{question}/answer/{answer}/like")
    Call<String> likeAnswer(@Header("Authorization") String token,
                            @Path("question") int questionId,
                            @Path("answer") int answerId);

    /**
     * 답변채택
     */
    @Headers({
            "Accept:application/json",
            "Content-Type:application/x-www-form-urlencoded"})
    @POST("question/{question}/answer/{answer}/select")
    Call<String> selectAnswer(@Header("Authorization") String token,
                              @Path("question") int questionId,
                              @Path("answer") int answerId);

    /**
     * 댓글관련
     */
    @Headers({
            "Accept:application/json",
            "Content-Type:application/x-www-form-urlencoded"})
    @POST("answer/{answer}/comment")
    Call<String> postComment(@Header("Authorization") String token,
                             @Path("answer") int answerId,
                             @Query("contents") String contents);

    @Headers({
            "Accept:application/json",
            "Content-Type:application/x-www-form-urlencoded"})
    @PUT("answer/{answer}/comment/{comment}")
    Call<String> updateComment(@Header("Authorization") String token,
                               @Path("answer") int answerId,
                               @Path("comment") int commentId);

    @Headers({
            "Accept:application/json",
            "Content-Type:application/x-www-form-urlencoded"})
    @DELETE("answer/{answer}/comment/{comment}")
    Call<String> deleteComment(@Header("Authorization") String token,
                               @Path("answer") int answerId,
                               @Path("comment") int commentId);
    @Headers({
            "Accept:application/json",
            "Content-Type:application/x-www-form-urlencoded"})
    @PUT("answer/{answer}/comment/{comment}/like")
    Call<String> likeComment(@Header("Authorization") String token,
                          @Path("answer") int answerId,
                          @Path("comment") int commentId);

}
