import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.urocareapp.R
import com.example.urocareapp.modelo.Consejo

class ConsejosAdapter(private val consejosList: List<Consejo>) :
    RecyclerView.Adapter<ConsejosAdapter.ConsejoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsejoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_consejo, parent, false)
        return ConsejoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConsejoViewHolder, position: Int) {
        val consejo = consejosList[position]
        holder.tituloTextView.text = consejo.titulo
        holder.descripcionTextView.text = consejo.descripcion
    }

    override fun getItemCount() = consejosList.size

    class ConsejoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tituloTextView: TextView = itemView.findViewById(R.id.tituloTextView)
        val descripcionTextView: TextView = itemView.findViewById(R.id.descripcionTextView)
    }
}
