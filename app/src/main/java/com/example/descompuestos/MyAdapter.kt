// MyAdapter.kt
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.descompuestos.R

class MyAdapter(private val dataList: List<String>,
                private val mImageUrls: List<String>,
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
        holder.bind(dataList[position])
        holder.bindImage(mImageUrls[position])
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
        private val textView: TextView = itemView.findViewById(R.id.rv_title)
        private val img: ImageView = itemView.findViewById(R.id.background_img)

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
    }
}