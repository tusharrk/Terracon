package com.terracon.survey.views.add_point_form_bio
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ArrayAdapter
//import android.widget.Filter
//import android.widget.GridLayout.Spec
//import android.widget.ImageView
//import android.widget.TextView
//import com.terracon.survey.R
//import com.terracon.survey.model.SpeciesNameDTO
//
//class CustomAutoCompleteAdapter(context: Context, private val itemLayout: Int, private val items: List<SpeciesNameDTO>) :
//    ArrayAdapter<SpeciesNameDTO>(context, itemLayout, items) {
//
//    private val inflater = LayoutInflater.from(context)
//
//    private var filteredItems: List<SpeciesNameDTO> = items
//
//    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//        val view = convertView ?: inflater.inflate(itemLayout, parent, false)
//
//       // val itemIcon = view.findViewById<ImageView?>(R.id.item_icon)
////        val itemText = view.findViewById<TextView?>(R.id.commonName)
////        val itemText1 = view.findViewById<TextView?>(R.id.scientificName)
////
////        // Set the data for the item
////       // itemIcon?.setImageResource(R.drawable.ic_placeholder)
////        itemText?.text = getItem(position)?.common_name
////        itemText1?.text = getItem(position)?.scientific_name
//
//        return view
//    }
//
//
//    override fun getFilter(): Filter {
//        return object : Filter() {
//            override fun performFiltering(constraint: CharSequence?): FilterResults {
//                val results = FilterResults()
//                val filterPattern = constraint?.toString()?.toLowerCase()?.trim() ?: ""
//
//                val filteredItemsList = if (filterPattern.isEmpty()) {
//                    items
//                } else {
//                    items.filter { item ->
//                        (item.common_name?.toLowerCase()?.contains(filterPattern) == true) ||
//                                (item.scientific_name?.toLowerCase()?.contains(filterPattern) == true)
//                    }
//                }
//
//                results.values = filteredItemsList
//                results.count = filteredItemsList.size
//                return results
//            }
//
//            @Suppress("UNCHECKED_CAST")
//            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
//                results?.values?.let {
//                    filteredItems = it as List<SpeciesNameDTO>
//                } ?: run {
//                    filteredItems = emptyList()
//                }
//
//                if (results?.count == 0) {
//                    notifyDataSetInvalidated()
//                } else {
//                    notifyDataSetChanged()
//                }
//            }
//        }
//    }
//}