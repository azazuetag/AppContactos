package com.mtw.alejandro.appcontactos

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mtw.alejandro.appcontactos.database.ContactoEntity
import kotlinx.android.synthetic.main.contacto_list_item.view.*
import java.util.*

class ContactoAdapter(private var mContactoEntries:List<ContactoEntity>,
                      private val mContext: Context, private val clickListener: (ContactoEntity) -> Unit)
    : RecyclerView.Adapter<ContactoAdapter.ContactoViewHolder>() {


    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new TaskViewHolder that holds the view for each task
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactoViewHolder {
        val layoutInflater = LayoutInflater.from(mContext)
        return ContactoViewHolder(layoutInflater.inflate(R.layout.contacto_list_item, parent, false))
    }

    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    override fun onBindViewHolder(holder: ContactoViewHolder, position: Int) {
        holder.bind(mContactoEntries[position], mContext, clickListener)
    }

    /**
     * Devuelve la cantidad de elementos para mostrar.
     */
    override fun getItemCount(): Int = mContactoEntries.size

    /**
     * Cuando los datos cambian, este metodo actualiza la lista de contactosEntries
     * y notifica al adaptador a usar estos nuevos valores
     */
    fun setTask(contactoEntries: List<ContactoEntity>){
        mContactoEntries = contactoEntries
        notifyDataSetChanged()
    }

    fun getTasks(): List<ContactoEntity> = mContactoEntries


    // Clase interna para crear ViewHolders
    class ContactoViewHolder (itemView: View) :RecyclerView.ViewHolder(itemView) {

        fun bind (contact:ContactoEntity, context: Context, clickListener: (ContactoEntity) -> Unit){
            //Asigna los valores a los elementos delcontacto_list_item
            itemView.tvNombre.text = contact.nomcontacto
            itemView.tvTelefono.text = contact.telcelular.toString()

            itemView.setOnClickListener{ clickListener(contact)}
        }
    }

}
