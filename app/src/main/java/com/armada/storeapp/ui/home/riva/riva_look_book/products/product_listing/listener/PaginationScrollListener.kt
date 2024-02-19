package com.armada.storeapp.ui.home.riva.riva_look_book.products.product_listing.listener

import androidx.recyclerview.widget.RecyclerView

/**
 * Created by USER on 5/16/2017.
 */
 abstract class PaginationScrollListener( var layoutManager: RecyclerView.LayoutManager?) : RecyclerView.OnScrollListener() {

    abstract val totalPageCount: Int
    var activeAdapter = 0
    abstract val isLoading: Boolean

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val visibleItemCount = recyclerView.layoutManager!!.childCount
        val totalItemCount = recyclerView.layoutManager!!.itemCount
        val firstVisibleItemPosition = (recyclerView.layoutManager as androidx.recyclerview.widget.GridLayoutManager).findFirstVisibleItemPosition()

        val firstCompletelyVisiblePost=(recyclerView.layoutManager as androidx.recyclerview.widget.GridLayoutManager).findFirstCompletelyVisibleItemPosition()
        //println("First Position: "+firstCompletelyVisiblePost)

        if (firstCompletelyVisiblePost==0 || dy >= 0) {
           hideUp()
        } else if (firstCompletelyVisiblePost>0 && dy < 0) {
          showUp()
        }

        //println("Here i am pagination loading  $isLoading")
       // if (!isLoading) {
            if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
                loadMoreItems()
            }
       // }


     //   if (activeAdapter != firstVisibleItemPosition) {

            try {

                loadVideo(recyclerView.layoutManager as androidx.recyclerview.widget.GridLayoutManager,firstVisibleItemPosition)

            } catch (e: NullPointerException) {
               // println("EXCEPTION: "+e.printStackTrace())
                // Sometimes you scroll so fast that the views are not attached so it gives a NullPointerException
            } catch (e: ArrayIndexOutOfBoundsException) {
               // println("EXCEPTION: "+e.printStackTrace())

            }
      //  }

            pauseVideo(recyclerView.layoutManager as androidx.recyclerview.widget.GridLayoutManager,firstVisibleItemPosition)
            activeAdapter = firstCompletelyVisiblePost

      //  }

    }

    protected abstract fun loadMoreItems()

    protected abstract fun showUp()

    protected abstract fun hideUp()

    protected abstract fun loadVideo(mLayoutManager: androidx.recyclerview.widget.GridLayoutManager,firstCompletelyVisibleItemPosition: Int)

    protected abstract fun pauseVideo(mLayoutManager: androidx.recyclerview.widget.GridLayoutManager,firstCompletelyVisibleItemPosition: Int)

}
