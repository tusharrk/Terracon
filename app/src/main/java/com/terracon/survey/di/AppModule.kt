package com.terracon.survey.di

import android.app.Application
import androidx.room.Room
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.terracon.survey.data.PointDataRepository
import com.terracon.survey.data.ProjectRepository
import com.terracon.survey.data.TreeAssessmentRepository
import com.terracon.survey.data.UserRepository
import com.terracon.survey.data.local.AppDatabase
import com.terracon.survey.data.local.PointDataDao
import com.terracon.survey.data.local.ProjectDao
import com.terracon.survey.data.local.TreeAssessmentDao
import com.terracon.survey.data.local.UserDao
import com.terracon.survey.data.remote.PointDataRemoteDataSource
import com.terracon.survey.data.remote.ProjectRemoteDataSource
import com.terracon.survey.data.remote.TreeAssessmentRemoteDataSource
import com.terracon.survey.data.remote.UserRemoteDataSource
import com.terracon.survey.network.services.PointDataService
import com.terracon.survey.network.services.ProjectService
import com.terracon.survey.network.services.TreeAssessmentService
import com.terracon.survey.network.services.UserService
import com.terracon.survey.utils.Config
import com.terracon.survey.views.add_point_form_bio.AddPointFormBioViewModel
import com.terracon.survey.views.bio_diversity_form_main.BioDiversityFormMainViewModel
import com.terracon.survey.views.flora_fauna.FloraFaunaViewModel
import com.terracon.survey.views.home.HomeViewModel
import com.terracon.survey.views.login.LoginViewModel
import com.terracon.survey.views.otp_verify.OtpVerifyViewModel
import com.terracon.survey.views.points_list.PointsListViewModel
import com.terracon.survey.views.project_details.ProjectDetailsViewModel
import com.terracon.survey.views.register.RegisterViewModel
import com.terracon.survey.views.splash.SplashViewModel
import com.terracon.survey.views.tree_assessment_details_form.TreeAssessmentDetailsFormViewModel
import com.terracon.survey.views.tree_assessment_form.TreeAssessmentFormViewModel
import com.terracon.survey.views.tree_points_list.TreePointsListViewModel
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val viewModelModule = module {
    viewModel { HomeViewModel(get()) }
    viewModel { SplashViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { OtpVerifyViewModel(get()) }
    viewModel { ProjectDetailsViewModel(get()) }
    viewModel { BioDiversityFormMainViewModel(get()) }
    viewModel { FloraFaunaViewModel(get()) }
    viewModel { AddPointFormBioViewModel(get(), get()) }
    viewModel { TreeAssessmentFormViewModel(get()) }
    viewModel { TreeAssessmentDetailsFormViewModel(get(), get()) }
    viewModel { PointsListViewModel(get()) }
    viewModel { TreePointsListViewModel(get()) }

}


val serviceModule = module {
    fun provideUserService(retrofit: Retrofit): UserService {
        return retrofit.create(UserService::class.java)
    }

    fun provideProjectService(retrofit: Retrofit): ProjectService {
        return retrofit.create(ProjectService::class.java)
    }

    fun providePointDataService(retrofit: Retrofit): PointDataService {
        return retrofit.create(PointDataService::class.java)
    }

    fun provideTreeDataService(retrofit: Retrofit): TreeAssessmentService {
        return retrofit.create(TreeAssessmentService::class.java)
    }

    single { provideUserService(get()) }
    single { provideProjectService(get()) }
    single { providePointDataService(get()) }
    single { provideTreeDataService(get()) }

}

val dataSourceModule = module {
    fun provideUserDataSource(userService: UserService, retrofit: Retrofit): UserRemoteDataSource {
        return UserRemoteDataSource(userService, retrofit)
    }

    fun provideProjectDataSource(
        projectService: ProjectService,
        retrofit: Retrofit
    ): ProjectRemoteDataSource {
        return ProjectRemoteDataSource(projectService, retrofit)
    }

    fun providePointDataSource(
        pointDataService: PointDataService,
        retrofit: Retrofit
    ): PointDataRemoteDataSource {
        return PointDataRemoteDataSource(pointDataService, retrofit)
    }

    fun provideTreeDataSource(
        treeAssessmentService: TreeAssessmentService,
        retrofit: Retrofit
    ): TreeAssessmentRemoteDataSource {
        return TreeAssessmentRemoteDataSource(treeAssessmentService, retrofit)
    }

    single { provideUserDataSource(get(), get()) }
    single { provideProjectDataSource(get(), get()) }
    single { providePointDataSource(get(), get()) }
    single { provideTreeDataSource(get(), get()) }


}

val netModule = module {

    fun provideCache(application: Application): Cache {
        val cacheSize = 10 * 1024 * 1024
        return Cache(application.cacheDir, cacheSize.toLong())
    }

    fun provideHttpClient(cache: Cache): OkHttpClient {
        val okHttpClientBuilder = OkHttpClient.Builder()
            .cache(cache)

        return okHttpClientBuilder.build()
    }

    fun provideGson(): Gson {
        return GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create()
    }


    fun provideRetrofit(factory: Gson, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Config.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(factory))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(client)
            .build()
    }

    single { provideCache(androidApplication()) }
    single { provideHttpClient(get()) }
    single { provideGson() }
    single { provideRetrofit(get(), get()) }

}

val databaseModule = module {

    fun provideDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(application, AppDatabase::class.java, "eds.database")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }


    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao
    }

    fun provideProjectDao(database: AppDatabase): ProjectDao {
        return database.projectDao
    }

    fun providePointDataDao(database: AppDatabase): PointDataDao {
        return database.pointDataDao
    }

    fun provideTreeDataDao(database: AppDatabase): TreeAssessmentDao {
        return database.treeAssessmentDao
    }

    single { provideDatabase(androidApplication()) }
    single { provideUserDao(get()) }
    single { provideProjectDao(get()) }
    single { providePointDataDao(get()) }
    single { provideTreeDataDao(get()) }

}

val repositoryModule = module {
    fun provideUserRepository(dataSource: UserRemoteDataSource, dao: UserDao): UserRepository {
        return UserRepository(dataSource, dao)
    }

    fun provideProjectRepository(
        dataSource: ProjectRemoteDataSource,
        dao: ProjectDao
    ): ProjectRepository {
        return ProjectRepository(dataSource, dao)
    }

    fun providePointDataRepository(
        dataSource: PointDataRemoteDataSource,
        dao: PointDataDao
    ): PointDataRepository {
        return PointDataRepository(dataSource, dao)
    }

    fun provideTreeRepository(
        dataSource: TreeAssessmentRemoteDataSource,
        dao: TreeAssessmentDao
    ): TreeAssessmentRepository {
        return TreeAssessmentRepository(dataSource, dao)
    }

    single { provideUserRepository(get(), get()) }
    single { provideProjectRepository(get(), get()) }
    single { providePointDataRepository(get(), get()) }
    single { provideTreeRepository(get(), get()) }
}