package com.terracon.survey.views.add_point_form_bio

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.terracon.survey.data.PointDataRepository
import com.terracon.survey.data.UserRepository
import com.terracon.survey.model.Project
import com.terracon.survey.model.SpeciesBio
import com.terracon.survey.views.bio_diversity_form_main.BioDiversityFormMainActivity
import com.terracon.survey.views.flora_fauna.FloraFaunaActivity


class AddPointFormBioViewModel(
    private val pointDataRepository: PointDataRepository,
) : ViewModel() {

    private val _speciesBioList = MutableLiveData<ArrayList<SpeciesBio>?>()
    val speciesBioList: MutableLiveData<ArrayList<SpeciesBio>?> = _speciesBioList
    lateinit var  project: Project

init {
    val tempList = arrayListOf<SpeciesBio>(SpeciesBio(speciesId = 0, speciesName = "", count = 0))
    _speciesBioList.value = tempList
}

    fun insertBlankItemInSpeciesList(){
        val tempList= _speciesBioList.value
        tempList?.add(SpeciesBio(speciesId = 0, speciesName = "", count = 0))
        _speciesBioList.value = tempList
    }

    fun updateCountValue(speciesBio: SpeciesBio,index:Int){
        _speciesBioList.value?.get(index)?.count = speciesBio.count
        _speciesBioList.value?.get(index)?.speciesName = speciesBio.speciesName
        _speciesBioList.value?.get(index)?.speciesId = speciesBio.speciesId
    }

    fun incrementSpeciesCount(speciesBio: SpeciesBio){
        val index = speciesBioList.value?.indexOf(speciesBio)
        index?.let { _speciesBioList.value?.get(it)?.count = speciesBio.count+1  }


    }
    fun decrementSpeciesCount(speciesBio: SpeciesBio){
        val index = speciesBioList.value?.indexOf(speciesBio)
        index?.let { _speciesBioList.value?.get(it)?.count = speciesBio.count-1  }

    }



}