package com.siiri.apm.ui.activity

import android.view.KeyEvent
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.Toast
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.StringUtils
import com.just.agentweb.AgentWeb
import com.siiri.apm.BuildConfig
import com.siiri.apm.R
import com.siiri.apm.app.event.EventBusTags
import com.siiri.apm.app.event.ReceiveMessageEvent
import com.siiri.apm.utils.UserUtils
import com.siiri.apm.webview.AndroidInterface
import com.siiri.push.JPushUtils
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : BaseActivity() {

    private var mAgentWeb: AgentWeb? = null

    private var exitTime = 0L

    private val mRxPermission by lazy { RxPermissions(this) }

    private var hasCheck = false

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initData() {
        initWebView()
    }

    private fun initWebView() {
        mAgentWeb = AgentWeb.with(this)
            .setAgentWebParent(frameLayout, FrameLayout.LayoutParams(-1, -1))
            .closeIndicator()
            .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
            .setWebChromeClient(object : com.just.agentweb.WebChromeClient() {

            })
            .setWebViewClient(object : com.just.agentweb.WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    if (!hasCheck) {
                        hasCheck = true
                        mRxPermission.request(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            .subscribe {
                                if (it) {
                                    mAgentWeb?.jsAccessEntrace?.quickCallJs("checkUpdate")
                                }
                            }
                    }

                }
            })
            .createAgentWeb()
            .ready()
            .go(BuildConfig.WEBVIEW_URL)
        mAgentWeb?.let {
            it.jsInterfaceHolder.addJavaObject("android", AndroidInterface(this, it))
            KeyboardUtils.registerSoftInputChangedListener(this) { height ->
                it.jsAccessEntrace.quickCallJs("onSoftInputChange", height.toString())
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (mAgentWeb?.handleKeyEvent(keyCode, event) != false) {
            return true
        }
        if (KeyEvent.KEYCODE_BACK == keyCode && event!!.action == KeyEvent.ACTION_DOWN) {
            exit()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun exit() {
        if (System.currentTimeMillis() - exitTime > DELAY_EXIT) {
            Toast.makeText(this, StringUtils.getString(R.string.exit_app), Toast.LENGTH_SHORT)
                .show()
            exitTime = System.currentTimeMillis()
        } else {
            AppUtils.exitApp()
        }
    }

    override fun onPause() {
        mAgentWeb?.webLifeCycle?.onPause()
        super.onPause()
    }

    override fun onResume() {
        mAgentWeb?.webLifeCycle?.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        mAgentWeb?.webLifeCycle?.onDestroy()
        KeyboardUtils.unregisterSoftInputChangedListener(window)
        super.onDestroy()
    }

    fun biometricPromptResult(result: Boolean) {
        mAgentWeb?.jsAccessEntrace?.quickCallJs("biometricPromptResult", result.toString())
    }

    fun setAliasPush(flag: Boolean) {
        val userPhone = UserUtils.getUserPhone() ?: ""
        LogUtils.dTag("JIGUANG", userPhone)
        if (userPhone.isEmpty().not())
            if (flag)
                JPushUtils.setAliasPush(this, userPhone)
            else
                JPushUtils.delAliasPush(this, userPhone)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public fun onEvent(event: ReceiveMessageEvent) {
        if (event.event == EventBusTags.RECEIVER_MSG) {
            mAgentWeb?.jsAccessEntrace?.quickCallJs("getMsgList")
        }
    }

    companion object {
        private const val DELAY_EXIT = 2000
    }

}