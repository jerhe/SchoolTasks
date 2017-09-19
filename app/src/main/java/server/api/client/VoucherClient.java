package server.api.client;

import server.api.Post;
import server.api.result.Result;

import static server.api.SchoolTask.SERVER;

/**
 * Created by 夜夜通宵 on 2017/9/12.
 */

public class VoucherClient {
    private final static String GET_AVAILABLE_VOUCHERS = SERVER + "voucher/getAvailableVoucherList";

    /**
     * 获取用户可用代金券
     */
    public static void getAvailableVouchers(Result result){
        Post.newPost()
                .url(GET_AVAILABLE_VOUCHERS)
                .result(result)
                .post();
    }
}
