package com.siterwell.sdk.action;

import android.graphics.Bitmap;

import com.siterwell.sdk.bean.AirQualityBean;
import com.siterwell.sdk.bean.DefaultDeviceBean;
import com.siterwell.sdk.bean.DeviceBean;
import com.siterwell.sdk.bean.DeviceStatusBean;
import com.siterwell.sdk.bean.FileBean;
import com.siterwell.sdk.bean.FirmwareBean;
import com.siterwell.sdk.bean.FolderBean;
import com.siterwell.sdk.bean.FolderListBean;
import com.siterwell.sdk.bean.GroupBean;
import com.siterwell.sdk.bean.JWTBean;
import com.siterwell.sdk.bean.MOAuthBean;
import com.siterwell.sdk.bean.NewDeviceBean;
import com.siterwell.sdk.bean.NewsBean;
import com.siterwell.sdk.bean.OAuthListBean;
import com.siterwell.sdk.bean.OAuthRequestBean;
import com.siterwell.sdk.bean.ProfileBean;
import com.siterwell.sdk.bean.RuleBean;
import com.siterwell.sdk.bean.UserFileBean;
import com.siterwell.sdk.bean.WeatherAirBean;
import com.siterwell.sdk.bean.WeatherBeanResultsNow;

import java.util.List;

/**
 * Created by Administrator on 2017/10/16.
 */

public class HekrUser {

    public interface GetDataListener {
        void getDataSuccess(Object object);

        void getDataFail(int errorCode);
    }


    /**
     * 3.18 获取图形验证码
     */
    public interface GetImgCaptchaListener {
        void getImgCaptchaSuccess(Bitmap bitmap);

        void getImgCaptchaFail(int errorCode);
    }


    /**
     * 3.19 校验图形验证码
     */
    public interface CheckCaptcha {
        void checkCaptchaSuccess(String captchaToken);

        void checkCaptchaFail(int errorCode);
    }


    /**
     * 3.1 发送短信验接口
     */
    public interface GetVerifyCodeListener {

        /**
         * 发送验证码成功
         */
        void getVerifyCodeSuccess();

        /**
         * 发送验证码失败
         *
         * @param errorCode 错误信息
         */
        void getVerifyCodeFail(int errorCode);
    }

    /**
     * 3.2 校验验证码接口/验证密保问题接口
     *
     * @author jin123d
     */
    public interface CheckVerifyCodeListener {

        /**
         * 校验验证码成功，返回成功字段
         *
         * @param phoneNumber 用户手机号
         * @param verifyCode  验证码
         * @param token       此token在注册时要使用
         * @param expireTime  token过期时间,此token的过期时间，在此时间之前没有注册，token会过期，必须重新获取、校验验证码
         */
        void checkVerifyCodeSuccess(String phoneNumber, String verifyCode, String token, String expireTime);

        /**
         * 发送验证码失败,返回错误信息
         *
         * @param errorCode 错误信息
         */
        void checkVerifyCodeFail(int errorCode);
    }

    /**
     * 3.3 /3.4 注册回调
     *
     * @author jin123d
     */
    public interface RegisterListener {
        /**
         * 注册成功
         *
         * @param uid 用户ID
         */
        void registerSuccess(String uid);

        /**
         * 注册失败
         *
         * @param errorCode 错误码
         */
        void registerFail(int errorCode);
    }


    /**
     * 3.5 使用账号登录接口
     *
     * @author jin123d
     */
    public interface LoginListener {
        /**
         * 登录成功
         */

        void loginSuccess(String str);

        /**
         * 注册失败
         *
         * @param errorCode 错误信息
         */
        void loginFail(int errorCode);
    }

    /**
     * 3.6 使用手机号重置密码
     */
    public interface ResetPwdListener {
        void resetSuccess();

        void resetFail(int errorCode);
    }


    /**
     * 3.7 修改密码接口
     */
    public interface ChangePwdListener {
        void changeSuccess();

        void changeFail(int errorCode);
    }


    /**
     * 3.8 修改用户手机号
     */
    public interface ChangePhoneNumberListener {
        void changePhoneNumberSuccess();

        void changePhoneNumberFail(int errorCode);
    }

    /**
     * 3.9 发送重置密码邮件接口
     */
    public interface SendResetPwdEmailListener {
        void sendSuccess();

        void sendFail(int errorCode);
    }

    /**
     * 3.10 重新发送激活邮件
     */
    public interface ReSendVerifiedEmailListener {
        void reSendVerifiedEmailSuccess();

        void reSendVerifiedEmailFail(int errorCode);
    }

    /**
     * 3.11 发送修改邮箱邮件
     */
    public interface SendChangeEmailListener {
        void sendChangeEmailSuccess();

        void sendChangeEmailFail(int errorCode);
    }

    /**
     * 3.13 移动端OAuth
     */
    public interface MOAuthListener {
        void mOAuthSuccess(MOAuthBean moAuthBean);

        void mOAuthSuccess(JWTBean jwtBean);

        void mOAuthFail(int errorCode);
    }

    /**
     * 3.14 将OAuth账号和主账号绑定
     */
    public interface BindOAuthListener {
        void bindSuccess();

        void bindFail(int errorCode);
    }

    /**
     * 3.17 创建匿名Hekr主账户并与当前登录三方账户绑定
     */
    public interface CreateUserAndBindListener {
        void createSuccess(String str);

        void createFail(int errorCode);
    }

    /**
     * 通过手机将第三方账号升级为hekr主账号
     */
    public interface AccountUpgradeListener {
        void UpgradeSuccess();

        void UpgradeFail(int errorCode);
    }

    /**
     * 发送校验邮件
     */
    public interface SendEmailListener {
        void sendSuccess();

        void sendFail(int errorCode);
    }

    public interface SetSecurityQuestionListener {
        void setSuccess();

        void setFail(int errorCode);
    }

    public interface IsSecurityAccountListener {
        void checkSuccess(boolean is);

        void checkFail(int errorCode);
    }

    /**
     * 4.1.1 绑定设备接口
     */
    public interface BindDeviceListener {

        /**
         * 绑定设备成功
         *
         * @param deviceBean 设备实体类
         */
        void bindDeviceSuccess(DeviceBean deviceBean);

        void bindDeviceFail(int errorCode);
    }

    /**
     * 4.1.2 列举设备列表接口
     */
    public interface GetDevicesListener {
        /**
         * 获取设备成功
         *
         * @param devicesLists 设备列表
         */
        void getDevicesSuccess(List<DeviceBean> devicesLists);

        void getDevicesFail(int errorCode);
    }

    /**
     * 4.1.3 删除设备接口
     */
    public interface DeleteDeviceListener {
        /**
         * 删除设备成功
         */
        void deleteDeviceSuccess();

        /**
         * 删除设备失败
         *
         * @param errorCode 错误信息
         */
        void deleteDeviceFail(int errorCode);
    }


    /**
     * 4.1.4 更改设备名称/描述接口
     */
    public interface RenameDeviceListener {
        /**
         * 改名成功
         */
        void renameDeviceSuccess();

        /**
         * 改名失败
         *
         * @param errorCode 错误信息
         */
        void renameDeviceFail(int errorCode);
    }

    /**
     * 4.1.5 获取当前局域网内所有设备绑定状态
     */
    public interface GetBindStatusListener {
        void getStatusSuccess(List<DeviceStatusBean> deviceStatusBeanLists);

        void getStatusFail(int errorCode);
    }


    /**
     * 4.1.5 获取当前局域网内所有设备绑定状态且直接进行绑定
     */
    public interface GetBindStatusAndBindListener {
        void getStatusSuccess(List<DeviceStatusBean> deviceStatusBeanLists);

        void getStatusFail(int errorCode);

        void bindDeviceSuccess(DeviceBean deviceBean);

        void bindDeviceFail(int errorCode);
    }

    /**
     * 4.1.8 GetQueryOwnerListener
     */
    public interface GetQueryOwnerListener {
        void queryOwnerSuccess(String message);

        void queryOwnerFail(int errorCode);
    }

    /**
     * 4.2.1 添加目录
     */
    public interface AddFolderListener {
        void addFolderSuccess(FolderBean folderBean);

        void addFolderFail(int errorCode);
    }

    /**
     * 4.2.2 列举目录
     */
    public interface GetFolderListsListener {
        void getSuccess(List<FolderListBean> folderList);

        void getFail(int errorCode);
    }


    /**
     * 4.2.3 修改目录名称
     */
    public interface RenameFolderListener {
        void renameSuccess();

        void renameFail(int errorCode);
    }

    /**
     * 4.2.4 删除目录
     */
    public interface DeleteFolderListener {
        void deleteSuccess();

        void deleteFail(int errorCode);
    }

    /**
     * 4.2.5 将设备挪到指定目录
     * <p>
     * 除了根目录，一个目录下最多可以放置512个设备
     * </p>
     */
    public interface DevicePutFolderListener {
        void putSuccess();

        void putFail(int errorCode);
    }


    /**
     * 4.3.2 反向授权创建
     * <br>
     * 1. 创建授权二维码URL
     * <br>
     * 在openApi中这个接口返回是"reverseAuthorizationTemplateId",SDK中直接将这个参数添加至一个url
     * (<a href="http://www.hekr.me?action=rauth&token=reverseAuthorizationTemplateId">二维码url</a>)中，
     * 因此创建二维码应该使用整个url进行创建，扫描解析时拿到url中的token参数来进行处理。
     * <br>
     */
    public interface CreateOAuthQRCodeListener {
        void createSuccess(String url);

        void createFail(int errorCode);
    }


    /**
     * 4.3.2 反向授权创建
     * <p>
     * 2. 被授权用户扫描二维码
     * </p>
     */
    public interface RegisterOAuthQRCodeListener {
        void registerSuccess();

        void registerFail(int errorCode);
    }


    /**
     * 4.3.2 反向授权创建
     * <p>
     * 3. 授权用户收到被授权者的请求
     * </p>
     */
    public interface GetOauthInfoListener {
        void getOauthInfoSuccess(List<OAuthRequestBean> lists);

        void getOauthInfoFail(int errorCode);
    }


    /**
     * 4.3.2 反向授权创建
     * <p>
     * 4. 授权用户同意
     * </p>
     */
    public interface AgreeOauthListener {
        void AgreeOauthSuccess();

        void AgreeOauthFail(int errorCode);
    }


    /**
     * 4.3.2 反向授权创建
     * <p>
     * 4. 授权用户拒绝
     * </p>
     */
    public interface RefuseOAuthListener {
        void refuseOauthSuccess();

        void refuseOauthFail(int errorCode);
    }


    /**
     * 4.3.4 取消授权
     */
    public interface CancelOAuthListener {
        void CancelOAuthSuccess();

        void CancelOauthFail(int errorCode);
    }

    /**
     * 4.3.5 列举授权信息
     */
    public interface GetOAuthListener {
        void getOAuthListSuccess(List<OAuthListBean> lists);

        void getOAuthListFail(int errorCode);
    }

    /**
     * 4.4.5 添加预约任务/4.4.5.1/4.4.5.2
     */
    public interface CreateRuleListener {
        void createSuccess();

        void createFail(int errorCode);
    }


    /**
     * 4.4.6 列举预约任务
     */
    public interface GetRulesListener {
        void getRulesSuccess(List<RuleBean> rules);

        void getRulesFail(int errorCode);
    }

    /**
     * 4.4.7 编辑预约任务
     * 4.4.8 删除预约任务
     */
    public interface OperationRuleListener {
        void operationRuleSuccess();

        void operationRuleFail(int errorCode);
    }

    /**
     * 4.5.1 获取用户档案
     */
    public interface GetProfileListener {
        void getProfileSuccess(ProfileBean profileBean);

        void getProfileFail(int errorCode);
    }


    /**
     * 4.5.2 更新用户档案
     */
    public interface SetProfileListener {
        void setProfileSuccess();

        void setProfileFail(int errorCode);
    }

    /**
     * 4.5.16 上传文件
     */
    public interface UploadFileListener {
        void uploadFileSuccess(FileBean fileBean);

        void uploadFileFail(int errorCode);

        void uploadProgress(int progress);
    }

    /**
     * 4.5.17 列举已上传文件
     */
    public interface GetFileListener {
        void getSuccess(UserFileBean fileBean);

        void getFail(int errorCode);
    }

    /**
     * 4.5.18 删除已上传文件
     */
    public interface DeleteFileListener extends DeleteFolderListener {

    }


    /**
     * 4.5.19 绑定推送标签接口
     */
    public interface PushTagBindListener {
        void pushTagBindSuccess();

        void pushTagBindFail(int errorCode);
    }

    /**
     * 4.5.20 解绑华为推送别名接口
     */
    public interface UnPushTagBindListener {
        void unPushTagBindSuccess();

        void unPushTagBindFail(int errorCode);
    }

    /***
     * 4.6.1 天气实况
     */
    public interface GetWeatherListener {
        void getWeatherSuccess(WeatherBeanResultsNow now, String location);

        void getWeatherFail(int errorCode);
    }

    /***
     * 4.6.3 空气质量实况
     */
    public interface GetAirQualityListener {
        void getAirQualitySuccess(AirQualityBean airQualityBean, String pm25);

        void getAirQualityFail(int errorCode);
    }

    /***
     * 天气+PM实况
     */
    public interface GetNewWeatherListener {
        void getSuccess(WeatherAirBean weatherAirBean);

        void getFail(int errorCode);
    }


    /**
     * 4.7.2 列举群组
     */
    public interface GetGroupListener {

        void getGroupSuccess(List<GroupBean> groupBeen);

        void getGroupFail(int errorCode);
    }


    /**
     * 4.7.3 群组改名
     * 4.7.4 删除群组
     */
    public interface OperationGroupListener {

        void OperationSuccess();

        void OperationGroupFail(int errorCode);
    }


    /**
     * 5.1 判断设备模块固件是否需要升级
     */
    public interface CheckFwUpdateListener {

        void checkNotNeedUpdate();

        void checkNeedUpdate(FirmwareBean firmwareBean);

        void checkFail(int errorCode);
    }


    /**
     * 5.2 根据pid获取企业资讯
     */
    public interface GetInfoListener {
        void getInfoSuccess(NewsBean newsBean);

        void getInfoFail(int errorCode);
    }

    /**
     * 5.5 售后管理 - 针对设备反馈问题
     */
    public interface FeedbackListener {
        void feedbackSuccess();

        void feedFail(int errorCode);
    }

    /**
     * 5.10 获取默认演示设备
     */
    public interface GetDefaultDevicesListener {
        void getSuccess(List<DefaultDeviceBean> list);

        void getFail(int errorCode);
    }

    /**
     * 获取配网PinCode
     */
    public interface GetPinCodeListener {
        void getSuccess(String pinCode);

        void getFail(int errorCode);

        void getFailInSuccess();
    }

    /**
     * 获取新增设备
     */
    public interface GetNewDevicesListener {
        void getSuccess(List<NewDeviceBean> list);

        void getFail(int errorCode);
    }

}
