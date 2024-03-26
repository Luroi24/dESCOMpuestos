// MyAdapter.kt
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.descompuestos.R
import com.example.descompuestos.Store

class MyAdapter(private val dataList: ArrayList<Store>,

                private val layoutResId: Int) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    private var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        return ViewHolder(view, onItemClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataList[position].storeName)                 //Cambia el nombre
        holder.bindImage(dataList[position].imageLinks[0])          //Cambia las imagenes
        holder.bindRating(dataList[position].ratingPlace.toString())
        holder.bindCategory(dataList[position].category)
        holder.bindFav(dataList[position].ratingPlace)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class ViewHolder(itemView: View, private val onItemClickListener: OnItemClickListener?) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                onItemClickListener?.onItemClick(adapterPosition)
            }
        }
        private val rat: TextView? = itemView?.findViewById<TextView>(R.id.rv_stars)
        private val textView: TextView = itemView.findViewById(R.id.rv_title)
        private val img: ImageView = itemView.findViewById(R.id.background_img)
        private val cat: TextView? = itemView?.findViewById(R.id.rv_nearby_subtitle)
        private val fav: TextView? = itemView?.findViewById(R.id.rv_fav)



        fun bind(data: String) {
            if(data.length >= 12){
                textView.text = "${data.substring(0, 9)}..."
            }
            else{textView.text = data}
        }
        fun bindImage(data: String){
            Glide.with(itemView)
                .load(data)
                .into(img)
        }

        fun bindRating(data: String) {
            if(data.length >= 3){
                rat?.text = "⭐"+"${data.substring(0,3)}";
            }
            else{rat?.text = "⭐"+ data}
        }

        fun bindCategory(data: String) {
            cat?.text = data;
        }


        fun bindFav(rating: Double) {
            if(rating >= 4.5){
                fav?.text="❤"
            }
            else{
                fav?.setBackgroundColor(Color.TRANSPARENT)
                fav?.text=""
            }
        }

    }
}