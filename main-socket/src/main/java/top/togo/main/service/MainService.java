package top.togo.main.service;


public interface MainService {


    /**
     * 匹配
     */
    void match(Long uid, Object msg, String type);


    /**
     * 取消匹配
     *
     * @param data 数据
     */
    void unMatch(Long uid, Object data);
}
