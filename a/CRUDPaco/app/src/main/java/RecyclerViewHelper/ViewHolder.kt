package RecyclerViewHelper

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import paco.crudpaco.R

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val textView: TextView = view.findViewById(R.id.txtProductoCard)


    val imgEditar: ImageView = view.findViewById(R.id.imgEditar)
    val imgborrar: ImageView = view.findViewById(R.id.imgEliminar)



}


