import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.armada.storeapp.R
import com.armada.storeapp.databinding.LvMultipleHeaderItemBinding
import com.armada.storeapp.ui.home.riva.riva_look_book.products.product_listing.model.ProductListMDataModel
import com.armada.storeapp.ui.utils.Constants
import com.armada.storeapp.ui.utils.Utils
import com.armada.storeapp.ui.utils.video_view.SimpleMainThreadMediaPlayerListener
import com.armada.storeapp.ui.utils.video_view.SingleVideoPlayerManager

class MultipleBannerAdapter(
    private val arrListMultipleProduct: ArrayList<ProductListMDataModel>?,
    private val mContext: Activity
) : RecyclerView.Adapter<MultipleBannerAdapter.MultipleBannerViewHolder>() {
    private var density = 0.0

    var strId: String = ""
    var strSizeGuide: String = ""

    init {
        val metrics = mContext.resources.displayMetrics
        density = (metrics.widthPixels.toDouble() / 320)

    }

    val mVideoPlayerManager =
        SingleVideoPlayerManager {

        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MultipleBannerViewHolder {
        return MultipleBannerViewHolder(
            LvMultipleHeaderItemBinding.inflate(
                LayoutInflater.from(
                    mContext
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MultipleBannerViewHolder, position: Int) {
        val currentItem = arrListMultipleProduct?.get(position)
        holder.binding.progressBannerMultiple?.visibility = View.VISIBLE
        holder.binding.relMainMultiple.visibility = View.VISIBLE
        val image_Height: Double = (currentItem?.custom_options?.toInt()?.times((density))) ?: 0.0
        val layoutWidth = (Utils.getDeviceWidth(mContext)
            .toDouble() - (mContext.resources.getDimension(R.dimen.corner_radius))) / 2
        holder.binding.imgBannerMultiple.visibility = View.VISIBLE

        (holder.binding.relMainMultiple.layoutParams as RecyclerView.LayoutParams).width =
            layoutWidth.toInt()
        //println("banner position:: " + position)
        if (position == 0)
            (holder.binding.relMainMultiple.layoutParams as RecyclerView.LayoutParams).marginEnd =
                mContext.resources.getDimension(R.dimen.corner_radius).toInt()
        else
            (holder.binding.relMainMultiple.layoutParams as RecyclerView.LayoutParams).marginEnd =
                0

        if (currentItem?.mediaType == "V") {
            if (!currentItem.mediaFile.isNullOrEmpty()) {
                holder.binding.myVideoMultiple.addMediaPlayerListener(object :
                    SimpleMainThreadMediaPlayerListener() {
                    override fun onVideoCompletionMainThread() {

                    }

                    override fun onVideoStoppedMainThread() {

                    }

                    override fun onVideoPreparedMainThread() {
                        holder.binding.myVideoMultiple.visibility = View.VISIBLE
                        holder.binding.progressBannerMultiple.visibility = View.GONE
                    }
                })
                mVideoPlayerManager.playNewVideo(
                    null,
                    holder.binding.myVideoMultiple,
                    currentItem.mediaFile
                )
            }
        } else {
            if (currentItem?.image != null && currentItem.image.contains(".gif")) {
                Utils.loadGifUsingCoilWithSize(
                    mContext,
                    if (currentItem.image != null && !currentItem.image.equals("")) currentItem.image else Constants.strNoImage,
                    holder.binding.imgBannerMultiple,
                    layoutWidth.toInt(),
                    image_Height.toInt()
                )

            } else {
                Utils.loadImagesUsingCoilWithSize(
                    mContext,
                    if (currentItem?.image != null && !currentItem?.image.equals("")) currentItem?.image else Constants.strNoImage,
                    holder.binding.imgBannerMultiple,
                    layoutWidth.toInt(),
                    image_Height.toInt()
                )
            }
        }


        holder.itemView.setOnClickListener()
        {
//            if (!currentItem?.id.isNullOrEmpty() && currentItem?.id != "null") {
//                val intent = Intent(mContext, ProductDetailActivity::class.java)
//                intent.putExtra("id", currentItem?.id)
//                intent.putExtra("cat_id", strId)
//                intent.putExtra("name", currentItem?.name)
//                intent.putExtra("size_guide", strSizeGuide)
//                //intent.putExtra("image", productArrList!![i].image_url)
//                mContext.startActivity(intent)
//
//            }todo
        }
    }

    override fun getItemCount(): Int {
        return arrListMultipleProduct?.size ?: 0
    }


    inner class MultipleBannerViewHolder(val binding: LvMultipleHeaderItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}