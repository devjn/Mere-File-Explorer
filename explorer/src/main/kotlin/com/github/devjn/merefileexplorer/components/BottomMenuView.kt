@file:Suppress("unused")

package com.github.devjn.merefileexplorer.components

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.support.annotation.DrawableRes
import android.support.annotation.IdRes
import android.support.design.R
import android.support.design.internal.BottomNavigationMenu
import android.support.design.internal.BottomNavigationMenuView
import android.support.design.internal.BottomNavigationPresenter
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v7.content.res.AppCompatResources
import android.support.v7.view.SupportMenuInflater
import android.support.v7.view.menu.MenuBuilder
import android.support.v7.widget.TintTypedArray
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.FrameLayout
import android.view.Gravity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

/**
 * Created by @author Jahongir on 14-Feb-17
 * devjn@jn-arts.com
 * BottomMenuView
 */
@SuppressLint("RestrictedApi","PrivateResource")
class BottomMenuView : FrameLayout {

    private val CHECKED_STATE_SET = intArrayOf(android.R.attr.state_checked)
    private val DISABLED_STATE_SET = intArrayOf(-android.R.attr.state_enabled)

    private val MENU_PRESENTER_ID = 1

    private var mMenu: MenuBuilder
    private var mMenuView: BottomNavigationMenuView
    private val mPresenter = BottomNavigationPresenter()
    private var mMenuInflater: MenuInflater? = null

    private var mSelectedListener: OnMenuItemSelectedListener? = null

    constructor(context: Context): this(context, null)

    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        // Create the menu
        mMenu = BottomNavigationMenu(context)

        mMenuView = BottomNavigationMenuView(context)
        val params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.gravity = Gravity.CENTER
        mMenuView.layoutParams = params

        mPresenter.setBottomNavigationMenuView(mMenuView)
        mPresenter.id = MENU_PRESENTER_ID
        mMenuView.setPresenter(mPresenter)
        mMenu.addMenuPresenter(mPresenter)
        mPresenter.initForMenu(getContext(), mMenu)

        // Custom attributes
        val a = TintTypedArray.obtainStyledAttributes(context, attrs,
                R.styleable.BottomNavigationView, defStyleAttr,
                R.style.Widget_Design_BottomNavigationView)

        if (a.hasValue(R.styleable.BottomNavigationView_itemIconTint)) {
            mMenuView.iconTintList = a.getColorStateList(R.styleable.BottomNavigationView_itemIconTint)
        } else {
            mMenuView.iconTintList = createDefaultColorStateList(android.R.attr.textColorSecondary)
        }
        if (a.hasValue(R.styleable.BottomNavigationView_itemTextColor)) {
            mMenuView.itemTextColor = a.getColorStateList(R.styleable.BottomNavigationView_itemTextColor)
        } else {
            mMenuView.itemTextColor = createDefaultColorStateList(android.R.attr.textColorSecondary)
        }
        if (a.hasValue(R.styleable.BottomNavigationView_elevation)) {
            ViewCompat.setElevation(this, a.getDimensionPixelSize(R.styleable.BottomNavigationView_elevation, 0).toFloat())
        }

        val itemBackground = a.getResourceId(R.styleable.BottomNavigationView_itemBackground, 0)
        mMenuView.itemBackgroundRes = itemBackground

        if (a.hasValue(R.styleable.BottomNavigationView_menu)) {
            inflateMenu(a.getResourceId(R.styleable.BottomNavigationView_menu, 0))
        }
        a.recycle()

        addView(mMenuView, params)
        if (Build.VERSION.SDK_INT < 21) {
            addCompatibilityTopDivider(context)
        }

        mMenu.setCallback(object : MenuBuilder.Callback {
            override fun onMenuItemSelected(menu: MenuBuilder, item: MenuItem): Boolean {
                return mSelectedListener?.onNavigationItemSelected(item) ?: false
            }

            override fun onMenuModeChange(menu: MenuBuilder) {}
        })
    }

    /**
     * Set a listener that will be notified when a bottom navigation item is selected. This listener
     * will also be notified when the currently selected item is reselected, unless an
     * [OnNavigationItemReselectedListener] has also been set.
     *
     * @param listener The listener to notify
     *
     * @see .setOnNavigationItemReselectedListener
     */
    fun setOnMenuItemSelectedListener(listener: OnMenuItemSelectedListener?) {
        mSelectedListener = listener
    }

    /**
     * Returns the [Menu] instance associated with this bottom navigation bar.
     */
    fun getMenu(): Menu {
        return mMenu
    }

    /**
     * Inflate a menu resource into this navigation view.
     *
     *
     * Existing items in the menu will not be modified or removed.
     *
     * @param resId ID of a menu resource to inflate
     */
    fun inflateMenu(resId: Int) {
        mPresenter.setUpdateSuspended(true)
        getMenuInflater().inflate(resId, mMenu)
        mPresenter.setUpdateSuspended(false)
        mPresenter.updateMenuView(true)
    }

    /**
     * @return The maximum number of items that can be shown in BottomNavigationView.
     */
    fun getMaxItemCount(): Int {
        return BottomNavigationMenu.MAX_ITEM_COUNT
    }

    /**
     * Returns the tint which is applied to our menu items' icons.
     *
     * @see .setItemIconTintList
     * @attr ref R.styleable#BottomNavigationView_itemIconTint
     */
    fun getItemIconTintList(): ColorStateList? {
        return mMenuView.iconTintList
    }

    /**
     * Set the tint which is applied to our menu items' icons.
     *
     * @param tint the tint to apply.
     *
     * @attr ref R.styleable#BottomNavigationView_itemIconTint
     */
    fun setItemIconTintList(tint: ColorStateList?) {
        mMenuView.iconTintList = tint
    }

    /**
     * Returns colors used for the different states (normal, selected, focused, etc.) of the menu
     * item text.
     *
     * @see .setItemTextColor
     * @return the ColorStateList of colors used for the different states of the menu items text.
     *
     * @attr ref R.styleable#BottomNavigationView_itemTextColor
     */
    fun getItemTextColor(): ColorStateList? {
        return mMenuView.itemTextColor
    }

    /**
     * Set the colors to use for the different states (normal, selected, focused, etc.) of the menu
     * item text.
     *
     * @see .getItemTextColor
     * @attr ref R.styleable#BottomNavigationView_itemTextColor
     */
    fun setItemTextColor(textColor: ColorStateList?) {
        mMenuView.itemTextColor = textColor
    }

    /**
     * Returns the background resource of the menu items.
     *
     * @see .setItemBackgroundResource
     * @attr ref R.styleable#BottomNavigationView_itemBackground
     */
    @DrawableRes
    fun getItemBackgroundResource(): Int {
        return mMenuView.itemBackgroundRes
    }

    /**
     * Set the background of our menu items to the given resource.
     *
     * @param resId The identifier of the resource.
     *
     * @attr ref R.styleable#BottomNavigationView_itemBackground
     */
    fun setItemBackgroundResource(@DrawableRes resId: Int) {
        mMenuView.itemBackgroundRes = resId
    }

    /**
     * Returns the currently selected menu item ID, or zero if there is no menu.
     *
     * @see .setSelectedItemId
     */
    @IdRes
    fun getSelectedItemId(): Int {
        return mMenuView.selectedItemId
    }

    /**
     * Set the selected menu item ID. This behaves the same as tapping on an item.
     *
     * @param itemId The menu item ID. If no item has this ID, the current selection is unchanged.
     *
     * @see .getSelectedItemId
     */
    fun setSelectedItemId(@IdRes itemId: Int) {
        val item = mMenu.findItem(itemId)
        if (item != null) {
            if (!mMenu.performItemAction(item, mPresenter, 0)) {
                item.isChecked = true
            }
        }
    }

    /**
     * Listener for handling selection events on bottom navigation items.
     */
    interface OnMenuItemSelectedListener {
        /**
         * Called when an item in the bottom navigation menu is selected.
         *
         * @param item The selected item
         *
         * @return true to display the item as the selected item and false if the item should not
         * be selected. Consider setting non-selectable items as disabled preemptively to
         * make them appear non-interactive.
         */
        fun onNavigationItemSelected(item: MenuItem): Boolean
    }


    private fun addCompatibilityTopDivider(context: Context) {
        val divider = View(context)
        divider.setBackgroundColor(ContextCompat.getColor(context, R.color.design_bottom_navigation_shadow_color))
        val dividerParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                resources.getDimensionPixelSize(R.dimen.design_bottom_navigation_shadow_height))
        divider.layoutParams = dividerParams
        addView(divider)
    }

    private fun getMenuInflater(): MenuInflater {
        if (mMenuInflater == null) {
            mMenuInflater = SupportMenuInflater(context)
        }
        return mMenuInflater!!
    }

    private fun createDefaultColorStateList(baseColorThemeAttr: Int): ColorStateList? {
        val value = TypedValue()
        if (!context.theme.resolveAttribute(baseColorThemeAttr, value, true)) {
            return null
        }
        val baseColor = AppCompatResources.getColorStateList(context, value.resourceId)
        if (!context.theme.resolveAttribute(android.support.v7.appcompat.R.attr.colorPrimary, value, true)) {
            return null
        }
        val colorPrimary = value.data
        val defaultColor = baseColor.defaultColor
        return ColorStateList(arrayOf(DISABLED_STATE_SET, CHECKED_STATE_SET, View.EMPTY_STATE_SET), intArrayOf(baseColor.getColorForState(DISABLED_STATE_SET, defaultColor), colorPrimary, defaultColor))
    }

    fun asd(): Unit {

    }


}