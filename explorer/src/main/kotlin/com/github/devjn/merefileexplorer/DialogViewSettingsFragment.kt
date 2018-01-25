package com.github.devjn.merefileexplorer

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.devjn.filemanager.FileManager
import com.github.devjn.filemanager.ViewStyle
import com.jn.arts.jnlibs.ui.List.BaseBindViewHolder
import com.jn.arts.jnlibs.ui.List.BindableItem
import com.jn.arts.jnlibs.ui.List.FlexBindRecyclerAdapter
import com.jn.arts.jnlibs.ui.List.controller.BindableItemController
import com.jn.arts.jnlibs.ui.List.controller.BindableItemProvider

/**
 * Created by @author Jahongir on 24-Feb-2018
 * devjn@jn-arts.com
 * DialogViewSettingsFragment
 */
class DialogViewSettingsFragment : DialogFragment() {

    private var listener: DismissListener? = null


    private var items: List<BindableItem<*>> = java.util.ArrayList()

    private lateinit var itemProvider: BindableItemProvider
    private lateinit var adapter: FlexBindRecyclerAdapter
    private lateinit var list: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.dialog_view_settings, container, false)

        list = root.findViewById<RecyclerView>(R.id.list)

        val viewTypesController = BindableItemController<Type>(VIEW_TYPE_ARRANGE) { item -> item.id.toLong() }
        val items = arrayListOf(Type(ViewStyle.DEFAULT_LIST, R.string.view_list), Type(ViewStyle.DEFAULT_GRID, R.string.view_grid))

        itemProvider = BindableItemProvider(arrayOf(viewTypesController), arrayOf<List<*>>(items));
        setupList()

        dialog.setTitle("View");

        return root
    }

    protected fun setupList() {
        items = itemProvider.getItems<Any>()
        adapter = FlexBindRecyclerAdapter(items, StyleBindController())
        adapter.isNotifyOnChange = false
        list.layoutManager = GridLayoutManager(list.context, 2)
        list.adapter = adapter
    }

    override fun dismiss() {
        listener?.onDismiss()
        super.dismiss()
    }

    internal fun onClick(type: Type) {
        when (type.id) {
            ViewStyle.DEFAULT_LIST -> FileManager.getInstance().config.setViewStyle(ViewStyle.DEFAULT_LIST)
            ViewStyle.DEFAULT_GRID -> FileManager.getInstance().config.setViewStyle(ViewStyle.DEFAULT_GRID)
        }
        dismiss()
    }

    internal inner class StyleBindController : FlexBindRecyclerAdapter.BindableController {
        override fun createViewHolder(parent: ViewGroup, viewType: Int): BaseBindViewHolder<*>? {
            when (viewType) {
                VIEW_TYPE_ARRANGE -> {
                    val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_view_style, parent, false)
                    return StyleViewHolder(view as ViewGroup)
                }
                VIEW_TYPE_SORT -> {

                    return null
                }
            }
            return null
        }
    }


    internal inner class StyleViewHolder(itemView: ViewGroup) : BaseBindViewHolder<Type>(itemView) {

        val textView: TextView

        var type: Type? = null

        init {
            textView = itemView.findViewById(R.id.text)
            itemView.setOnClickListener({ type?.let { onClick(it) } })
        }

        override fun onBind(item: Type) {
            this.type = item
            textView.setText(item.textId)
        }

    }

    internal inner class Type(val id: Int, val textId: Int)


    companion object {

        const val VIEW_TYPE_ARRANGE = 0
        const val VIEW_TYPE_SORT = 1

        fun show(fragmentManager: FragmentManager) {
            val dialog = DialogViewSettingsFragment()
            dialog.show(fragmentManager, "DialogViewSettingsFragment")
        }

        fun show(fragmentManager: FragmentManager, dismissListener: DismissListener) {
            val dialog = DialogViewSettingsFragment()
            dialog.listener = dismissListener
            dialog.show(fragmentManager, "DialogViewSettingsFragment")
        }

    }

    interface DismissListener {
        fun onDismiss()
    }

}