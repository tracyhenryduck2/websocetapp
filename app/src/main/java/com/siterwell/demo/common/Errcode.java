package com.siterwell.demo.common;

import android.content.Context;

import com.siterwell.demo.R;


/**
 * Created by TracyHenry on 2018/2/6.
 */

public class Errcode {

    /**
     * 错误码转换到错误信息！
     *
     * @param errorCode 错误码
     * @return 错误信息
     */
    public static String errorCode2Msg(Context context, int errorCode) {
        switch (errorCode) {
            case 3400001:
                return context.getResources().getString(R.string.phone_number_invalid);
            case 3400002:
                return context.getResources().getString(R.string.verify_code_error);
            case 3400003:
                return context.getResources().getString(R.string.validation_code_expired);
            case 3400005:
                return context.getResources().getString(R.string.too_many_verification_codes);
            case 3400006:
                return context.getResources().getString(R.string.invalid_request_type);
            case 3400007:
                return context.getResources().getString(R.string.invalid_old_password);
            case 3400008:
                return context.getResources().getString(R.string.has_been_registered);
            case 3400009:
                return context.getResources().getString(R.string.has_not_validated_yet);
            case 3400010:
                return context.getResources().getString(R.string.account_or_password_error);
            case 3400011:
                return context.getResources().getString(R.string.user_does_not_exist);
            case 3400012:
                return context.getResources().getString(R.string.invalid_message_token);
            case 3400013:
                return context.getResources().getString(R.string.account_has_been_authenticated);
            case 3400014:
                return context.getResources().getString(R.string.account_has_been_associated_with_a_three_party_account);
            case 500:
            case 5200000:
                return context.getResources().getString(R.string.service_internal_error);
            case 5400001:
                return context.getResources().getString(R.string.internal_error);
            case 5400002:
                return context.getResources().getString(R.string.app_repeat_logon);
            case 5400003:
                return context.getResources().getString(R.string.appTid_cannot_be_empty);
            case 5400004:
                return context.getResources().getString(R.string.authorization_relationship_already_exists);
            case 5400005:
                return context.getResources().getString(R.string.authorization_relationship_does_not_exist);
            case 5400006:
                return context.getResources().getString(R.string.binding_failed_due_to_network_reasons);
            case 5400007:
                return context.getResources().getString(R.string.binding_failed_due_to_timeout_reason);
            case 5400009:
                return context.getResources().getString(R.string.failed_to_modify_user_file);
            case 5400010:
                return context.getResources().getString(R.string.failed_to_verify_code);
            case 5400011:
                return context.getResources().getString(R.string.device_authorization_times_are_capped);
            case 5400012:
                return context.getResources().getString(R.string.failed_due_to_internal_error_binding);
            case 5400013:
                return context.getResources().getString(R.string.binding_failed_because_of_repeated_binding);
            case 5400014:
                return context.getResources().getString(R.string.device_does_not_belong_to_the_user);
            case 5400015:
                return context.getResources().getString(R.string.there_is_no_such_instruction);
            case 5400016:
                return context.getResources().getString(R.string.device_cannot_log_in_again);
            case 5400017:
                return context.getResources().getString(R.string.devTid_cannot_be_empty);
            case 5400018:
                return context.getResources().getString(R.string.create_timed_reservation_times_to_upper_limit);
            case 5400019:
                return context.getResources().getString(R.string.authorized_instruction_has_expired);
            case 5400020:
                return context.getResources().getString(R.string.this_instruction_is_not_supported);
            case 5400021:
                return context.getResources().getString(R.string.illegal_mail_token);
            case 5400022:
                return context.getResources().getString(R.string.illegal_old_password);
            case 5400023:
                return context.getResources().getString(R.string.illegal_checksum_code);
            case 5400024:
                return context.getResources().getString(R.string.device_cannot_be_found_due_to_an_internal_error);
            case 5400025:
                return context.getResources().getString(R.string.pid_does_not_exist);
            case 5400026:
                return context.getResources().getString(R.string.there_is_no_authority_on_this_instruction);
            case 5400027:
                return context.getResources().getString(R.string.specified_template_does_not_exist);
            case 5400028:
                return context.getResources().getString(R.string.device_cannot_be_found_due_to_an_incorrect_internal_condition);
            case 5400035:
                return context.getResources().getString(R.string.the_specified_task_does_not_exist);
            case 5400036:
                return context.getResources().getString(R.string.unable_to_create_duplicate_template);
            case 5400037:
                return context.getResources().getString(R.string.deviceid_does_not_match);
            case 5400039:
                return context.getResources().getString(R.string.user_does_not_exist2);
            case 5400040:
                return context.getResources().getString(R.string.verify_that_code_expires);
            case 5400041:
                return context.getResources().getString(R.string.check_code_failed_to_send);
            case 5400042:
                return context.getResources().getString(R.string.verify_that_the_code_type_is_not_valid);
            case 5400043:
                return context.getResources().getString(R.string.device_cannot_bind_forcibly);
            case 5500000:
                return context.getResources().getString(R.string.internal_service_error);
            case 6400001:
                return context.getResources().getString(R.string.reverse_registration_request_for_the_specified_id_does_not_exist);
            case 6400002:
                return context.getResources().getString(R.string.Illegal_reverse_licensing_request);
            case 6400003:
                return context.getResources().getString(R.string.only_the_owner_can_authorize_the_equipment_to_other_people);
            case 6400004:
                return context.getResources().getString(R.string.the_device_specified_for_devTid_does_not_exist);
            case 6400005:
                return context.getResources().getString(R.string.upper_limit_on_the_number_of_devices_that_can_be_accommodated_by_a_folder);
            case 6400006:
                return context.getResources().getString(R.string.cannot_create_folder_with_the_same_name);
            case 6400007:
                return context.getResources().getString(R.string.folder_specified_for_id_does_not_exist);
            case 6400008:
                return context.getResources().getString(R.string.reached_the_maximum_number_of_folders_created);
            case 6400009:
                return context.getResources().getString(R.string.root_directory_cannot_be_deleted);
            case 6400010:
                return context.getResources().getString(R.string.root_directory_cannot_be_renamed);
            case 6400011:
                return context.getResources().getString(R.string.specified_rule_does_not_exist);
            case 6400012:
                return context.getResources().getString(R.string.specified_timed_reservation_task_does_not_exist);
            case 6400013:
                return context.getResources().getString(R.string.unable_to_create_the_same_rule);
            case 6400014:
                return context.getResources().getString(R.string.The_same_timed_reservation_cannot_be_created);
            case 6400015:
                return context.getResources().getString(R.string.illegal_prodPubKey);
            case 6400016:
                return context.getResources().getString(R.string.there_is_no_authority_to_do_so);
            case 6400017:
                return context.getResources().getString(R.string.request_parameter_error);
            case 6400018:
                return context.getResources().getString(R.string.specified_SkyDrive_file_does_not_exist);
            case 6400020:
                return context.getResources().getString(R.string.infrared_code_cannot_be_found);
            case 6400021:
                return context.getResources().getString(R.string.ir_service_request_error);
            case 6400022:
                return context.getResources().getString(R.string.cannot_find_instruction_set);
            case 6400023:
                return context.getResources().getString(R.string.parameter_not_supported);
            case 6400024:
                return context.getResources().getString(R.string.parsing_JSON_failed);
            case 6500001:
                return context.getResources().getString(R.string.failed_to_delete_SkyDrive_file);
            case 6500002:
                return context.getResources().getString(R.string.failed_to_upload_SkyDrive_file);
            case 6500003:
                return context.getResources().getString(R.string.HTTP_network_call_failed);
            case 8200000:
                return context.getResources().getString(R.string.success_e);
            case 8400000:
                return context.getResources().getString(R.string.product_does_not_exist);
            case 8400001:
                return context.getResources().getString(R.string.protocol_template_does_not_exist);
            case 8400002:
                return context.getResources().getString(R.string.illegal_parameter);
            case 8400003:
                return context.getResources().getString(R.string.platform_parameter_error);
            case 8400004:
                return context.getResources().getString(R.string.specifies_that_PID_does_not_exist);
            case 9200000:
                return context.getResources().getString(R.string.success_e2);
            case 9400000:
                return context.getResources().getString(R.string.error_e);
            case 9400001:
                return context.getResources().getString(R.string.illegal_parameter2);
            case 9400002:
                return context.getResources().getString(R.string.action_does_not_exist);
            case 9400003:
                return context.getResources().getString(R.string.product_does_not_exist2);
            case 9400004:
                return context.getResources().getString(R.string.timeout);
            case 9400005:
                return context.getResources().getString(R.string.method_does_not_support);
            case 9500000:
                return context.getResources().getString(R.string.service_error);
            case 9500001:
                return context.getResources().getString(R.string.service_response_error);
            case 0:
                return context.getResources().getString(R.string.network_timeout);
            case 1:
                return context.getResources().getString(R.string.logon_information_expired_log_in_again);
            case 2:
                return context.getResources().getString(R.string.unknown_error);
            case 400016:
                return context.getResources().getString(R.string.operation_is_too_frequent_try_again_later);
            case 400017:
                return context.getResources().getString(R.string.today_operation_has_reached_an_upper_limit);
          /*  case 400014:
                return "密码重置失败";*/
            case 11001:
                return context.getResources().getString(R.string.info_format_error);
            case 11002:
                return context.getResources().getString(R.string.info_null_error);
            case 11003:
                return context.getResources().getString(R.string.app_id_empty);
            case 11004:
                return context.getResources().getString(R.string.no_params_parameter);
            case 11005:
                return context.getResources().getString(R.string.no_connect);
            case 12001:
                return context.getResources().getString(R.string.net_error);
            case 13001:
                return context.getResources().getString(R.string.local_net_connect_error);
            case 13002:
                return context.getResources().getString(R.string.local_net_auth_timeout);
            case 20001:
                return context.getResources().getString(R.string.no_find_device);
            case 30001:
                return context.getResources().getString(R.string.page_loading_error);
            default:
                // return String.valueOf(errorCode);
                return context.getResources().getString(R.string.server_exception_try_again) + errorCode;
        }
    }

}
