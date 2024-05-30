package RecyclerViewHelper

import Modelo.ClaseConexion
import Modelo.dataClassProductos
import android.app.AlertDialog
import android.app.Dialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import paco.crudpaco.R
import java.nio.file.attribute.AclEntry.Builder

class Adaptador(private var Datos: List<dataClassProductos>) : RecyclerView.Adapter<ViewHolder>() {

    fun actualizarLista(nuevaLista: List<dataClassProductos>){
        Datos = nuevaLista
        notifyDataSetChanged()

    }

    //Funcion para actualizar el recycler view
    // cuando actualizo los datos

    fun actualizarListaDespuesDeAcualizarDatos (uuid: String, nuevoNombre: String){

        val index = Datos.indexOfFirst { it.uuid == uuid }
        Datos[index].nombreProducto = nuevoNombre
        notifyItemChanged(index)
    }

    fun eliminarRegistro(nombreProducto: String, posicion: Int){


        //quitar elemento de la lista
        val listaDatos = Datos.toMutableList()
        listaDatos.removeAt(posicion)

        // quitar de la base de datos

        GlobalScope.launch(Dispatchers.IO) {

            //-1 Crear un objeto de la clase conexion

            val objConexion = ClaseConexion().cadenaConexion()


            val delProducto = objConexion?.prepareStatement("delete tbProductos where nombreProducto = ? ")!!
            delProducto.setString(1,nombreProducto)
            delProducto.executeUpdate()



            val commit = objConexion.prepareStatement("commit")!!
            commit.executeUpdate()
        }
        //le decimos al adaptador  que se eliminaron datos
        Datos = listaDatos.toList()
        notifyItemRemoved(posicion)
        notifyDataSetChanged()
    }


    fun actualizarProductos(nombreProducto: String, uuid: String){

        // 1- Creo una corutina
        GlobalScope.launch (Dispatchers.IO){

            // 1
            val objConexion = ClaseConexion().cadenaConexion()


            val updateProductos = objConexion?.prepareStatement("update tbproductos set nombreProducto = ? where uuid = ?")!!
            updateProductos.setString(1, nombreProducto)
            updateProductos.setString(2, uuid)

            updateProductos.executeUpdate()



            val commit = objConexion?.prepareStatement("commit")!!

            commit.executeUpdate()


            withContext(Dispatchers.Main) {

                actualizarListaDespuesDeAcualizarDatos(uuid, nombreProducto)

            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista =
            LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return ViewHolder(vista)    }
    override fun getItemCount() = Datos.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = Datos[position]
        holder.textView.text = producto.nombreProducto



        val item = Datos[position]



        holder.imgborrar.setOnClickListener {

            //creamos una alerta

            //1- invocamos el contexto


           val context = holder.itemView.context

            //creo la alerta

            val builder = AlertDialog.Builder(context)

            // a mi alerta le pongo un titulo

            builder.setTitle("¿Esta seguro?")

            //ponerle un mensaje

            builder.setMessage("¿Deseas eliminar el registro?")

            //paso final, agregamos los botones

            builder.setPositiveButton("si") { Dialog, which ->
                eliminarRegistro(item.nombreProducto, position )
            }

            builder.setNegativeButton("no") { Dialog, which ->

            }

            //creamos la alerta

            val alertDialog = builder.create()

            //mostramos la alerta
            alertDialog.show()
        }




        holder.imgEditar.setOnClickListener {

            val context = holder.itemView.context

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Editar Nombre")


            val cuadritoNuevoNombre = EditText(context)
            cuadritoNuevoNombre.setHint(item.nombreProducto)
            builder.setView(cuadritoNuevoNombre)



            builder.setPositiveButton("Actualizar") {
                Dialog, wich ->

                actualizarProductos(cuadritoNuevoNombre.text.toString(),item.uuid)
            }


            builder.setNegativeButton("cancelar") {
                dialog, wich ->

                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()

        }


        //darle click  a la card

        holder.itemView.setOnClickListener {



        }

    }

}