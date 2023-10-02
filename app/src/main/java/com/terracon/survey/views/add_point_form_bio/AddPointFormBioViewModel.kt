package com.terracon.survey.views.add_point_form_bio

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.terracon.survey.R
import com.terracon.survey.data.PointDataRepository
import com.terracon.survey.data.ProjectRepository
import com.terracon.survey.model.BioPoint
import com.terracon.survey.model.BioPointDetails
import com.terracon.survey.model.ErrorState
import com.terracon.survey.model.PointDTO
import com.terracon.survey.model.Project
import com.terracon.survey.model.Result
import com.terracon.survey.model.Species
import com.terracon.survey.utils.FileHelper
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


class AddPointFormBioViewModel(
    private val pointDataRepository: PointDataRepository, private val projectRepository: ProjectRepository
) : ViewModel() {

    private val _speciesBioList = MutableLiveData<ArrayList<Species>?>()
    val speciesBioList: MutableLiveData<ArrayList<Species>?> = _speciesBioList

    private val _floraFaunaSpeciesList = MutableLiveData<List<String>?>()
    val floraFaunaSpeciesList: MutableLiveData<List<String>?> = _floraFaunaSpeciesList


    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorState = MutableLiveData<ErrorState?>()
    val errorState: LiveData<ErrorState?> = _errorState

    private val _isLoadingFullScreen = MutableLiveData<Boolean>()
    val isLoadingFullScreen: LiveData<Boolean> = _isLoadingFullScreen

    lateinit var project: Project
    lateinit var pointBio: BioPoint
    var pointBioDetails: BioPointDetails = BioPointDetails()

    lateinit var type:String
    lateinit var subType:String

    var isEdit = false
    var isEditIndex: Int? = null
    var imageUrl: String = ""

    init {
       // getSpeciesNamesList()
       // val tempList = arrayListOf<Species>(Species(id = 0, name = "", count = "0", imageUrl = "", comment = ""))
       // _speciesBioList.value = tempList
    }

     fun getSpeciesNamesList(){
        _isLoadingFullScreen.value = true
        _errorState.value = null
        viewModelScope.launch {
            projectRepository.getFloraFaunaSpeciesListBasedOnType(type,subType)
                .collect {
                    when (it?.status) {
                        Result.Status.SUCCESS -> {
                            _isLoadingFullScreen.value = false
                            _errorState.value = null
                            if (it.data != null) {
                                _floraFaunaSpeciesList.value = it.data
                                getPointDetailsData()
                            }
                            else {
                                _errorState.value = ErrorState.NoData
                            }
                        }

                        Result.Status.LOADING -> {
                            _isLoadingFullScreen.value = true
                            _errorState.value = null
                        }

                        Result.Status.ERROR -> {
                            _isLoadingFullScreen.value = false
                            _errorState.value = ErrorState.ServerError
                        }

                        else -> _errorState.value = ErrorState.NoData
                    }
                }
        }
    }

    private fun getPointDetailsData(){
        _isLoadingFullScreen.value = true
        //_errorState.value = null
        viewModelScope.launch {
            pointDataRepository.getPointDetailsFromLocal(PointDTO(pointBio.dbId.toString(),type,subType))
                .collect {
                    when (it?.status) {
                        Result.Status.SUCCESS -> {
                            _isLoadingFullScreen.value = false
                            _errorState.value = null
                            if (it.data?.status == "success") {
                                Log.d("TAG_X", it.data.toString())
                                if(it.data.data.bio_diversity_survey_data_points != null){
                                    _speciesBioList.value = ArrayList(it.data.data.bio_diversity_survey_data_points.species)
                                }
                            } else {

                               // _errorState.value = ErrorState.NoData
                            }
                        }

                        Result.Status.LOADING -> {
                            _isLoadingFullScreen.value = true
                            //_errorState.value = null
                        }

                        Result.Status.ERROR -> {
                            _isLoadingFullScreen.value = false
                            //_errorState.value = ErrorState.ServerError


                        }

                        else -> {
                            // _errorState.value = ErrorState.NoData
                        }
                    }
                }
        }
    }

    fun addItemToList(speciesBio: Species){
        if (_speciesBioList.value.isNullOrEmpty()){
             val tempList = arrayListOf<Species>(speciesBio)
             _speciesBioList.value = tempList
        }else{
            val tempList = _speciesBioList.value
            tempList?.add(speciesBio)
            _speciesBioList.value = tempList
        }
    }
    fun deleteItemFromList(index: Int,item:Species) {
        val tempList = _speciesBioList.value
        tempList?.removeAt(index)
        _speciesBioList.value = tempList
    }
    fun insertBlankItemInSpeciesList() {
        val tempList = _speciesBioList.value
        tempList?.add(Species(id = 0, name = "", count = "0"))
        _speciesBioList.value = tempList
    }

    fun updateCountValue(speciesBio: Species, index: Int) {
        _speciesBioList.value?.get(index)?.count = speciesBio.count
        _speciesBioList.value?.get(index)?.name = speciesBio.name
        _speciesBioList.value?.get(index)?.comment = speciesBio.comment
        _speciesBioList.value?.get(index)?.images = speciesBio.images
    }

    fun getSpeciesList():ArrayList<Species>{
        val list: ArrayList<Species> = arrayListOf<Species>()
        if(_speciesBioList.value!=null){
            list.addAll(_speciesBioList.value!!)
//           if(list[list.size-1].name.isBlank()){
//               list.removeLastOrNull()
//           }
        }
        return list
    }

    fun savePointData(activity: AddPointFormBioActivity){
        _isLoading.value = true
        viewModelScope.launch {
            pointDataRepository.saveBioPointDetailsInLocalDB(PointDTO(pointBio.dbId.toString(),type,subType),pointBioDetails)
                .collect {
                    when (it?.status) {
                        Result.Status.SUCCESS -> {
                            _isLoading.value = false
                            if (it.data != null) {
                                Log.d("TAG_X", it.data.toString())
                                activity.runOnUiThread {
                                    //val msg = it.data.message
                                    Toast.makeText(
                                        activity,
                                        "success",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    activity.finish()
                                }
                            } else {
                                activity.runOnUiThread {
                                    Toast.makeText(
                                        activity,
                                        "error",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                //_errorState.value = ErrorState.NoData
                            }
                        }

                        Result.Status.LOADING -> {
                            _isLoading.value = true
                        }

                        Result.Status.ERROR -> {
                            activity.runOnUiThread {
                                val errorMsg =
                                    if (it.error?.message != null) it.error.message else activity.resources.getString(
                                        R.string.server_error_desc
                                    )
                                Toast.makeText(
                                    activity,
                                    errorMsg,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }


                            _isLoading.value = false
                        }

                        else -> {
                            // _errorState.value = ErrorState.NoData
                        }
                    }
                }
        }
    }

//    fun fileUpload(activity: AddPointFormBioActivity) {
//        val requestBody = HashMap<String, RequestBody>()
//        requestBody["fileName"] =
//            "testing11112121122.jpeg".toRequestBody("text/plain".toMediaTypeOrNull())
//        val file: File? =  FileHelper.getFile(activity, Uri.parse(imageUrl))
//
//        if (file != null) {
//        // val f: File = activity.getFile(activity, imageUrl)
//        val fileBody: RequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
//        val imagePart: MultipartBody.Part =
//            MultipartBody.Part.createFormData("fileToUpload", file.name, fileBody)
//
//        viewModelScope.launch {
//            pointDataRepository.fileUpload(requestBody, imagePart)
//                .collect {
//                    when (it?.status) {
//                        Result.Status.SUCCESS -> {
//                            _isLoading.value = false
//                            if (it.data?.status == "success") {
//                                Log.d("TAG_X", it.data.toString())
//                                activity.runOnUiThread {
//                                    val msg = it.data.message
//                                    Toast.makeText(
//                                        activity,
//                                        msg,
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                    activity.finish()
//                                }
//                            } else {
//                                activity.runOnUiThread {
//                                    Toast.makeText(
//                                        activity,
//                                        it.data?.message,
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                }
//                                //_errorState.value = ErrorState.NoData
//                            }
//                        }
//
//                        Result.Status.LOADING -> {
//                            _isLoading.value = true
//                        }
//
//                        Result.Status.ERROR -> {
//                            activity.runOnUiThread {
//                                val errorMsg =
//                                    if (it.error?.message != null) it.error.message else activity.resources.getString(
//                                        R.string.server_error_desc
//                                    )
//                                Toast.makeText(
//                                    activity,
//                                    errorMsg,
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                            _isLoading.value = false
//                        }
//
//                        else -> {
//                            // _errorState.value = ErrorState.NoData
//                        }
//                    }
//                }
//        }
//    }
//    }


}









