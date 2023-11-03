package com.terracon.survey.views.add_point_form_bio

import android.content.Context
import android.icu.util.UniversalTimeScale.toLong
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.terracon.survey.R
import com.terracon.survey.model.SpeciesNameDTO

class PoiAdapter(context: Context, @LayoutRes private val layoutResource: Int, private val allPois: List<SpeciesNameDTO>):
    ArrayAdapter<SpeciesNameDTO>(context, layoutResource, allPois),
    Filterable {
    private var mPois: List<SpeciesNameDTO> = allPois

    override fun getCount(): Int {
        return mPois.size
    }

    override fun getItem(p0: Int): SpeciesNameDTO? {
        return mPois.get(p0)

    }
    override fun getItemId(p0: Int): Long {
        return p0.toLong()
       // return mPois.get(p0).scientific_name.toString()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView as View? ?: LayoutInflater.from(context).inflate(layoutResource, parent, false) as View
        val itemText = view.findViewById<TextView?>(R.id.item)
        itemText.text = "${mPois[position].common_name} (${mPois[position].scientific_name})"
        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(charSequence: CharSequence?, filterResults: Filter.FilterResults) {
                mPois = filterResults.values as List<SpeciesNameDTO>
                notifyDataSetChanged()
            }

            override fun performFiltering(charSequence: CharSequence?): Filter.FilterResults {
                val queryString = charSequence?.toString()?.toLowerCase()

                val filterResults = Filter.FilterResults()
                filterResults.values = if (queryString.isNullOrEmpty())
                    allPois
                else {
                    allPois.filter {
                        val common = it.common_name?.toLowerCase() ?: ""
                        val scientific = it.scientific_name?.toLowerCase() ?: ""

                        common.contains(queryString) ||
                                scientific.contains(queryString)
                    }
                }

                return filterResults
            }

        }
    }
}
