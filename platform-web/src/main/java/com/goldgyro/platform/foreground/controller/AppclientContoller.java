package com.goldgyro.platform.foreground.controller;

import com.goldgyro.platform.core.client.domain.BankCard;
import com.goldgyro.platform.core.client.domain.Customer;
import com.goldgyro.platform.core.client.entity.MerchantPO;
import com.goldgyro.platform.core.client.entity.VersionInfo;
import com.goldgyro.platform.core.client.service.BankCardService;
import com.goldgyro.platform.core.client.service.CustomerService;
import com.goldgyro.platform.core.client.service.MerchantService;
import com.goldgyro.platform.core.client.service.VersionInfoService;
import com.goldgyro.platform.core.comm.domain.InterfaceResponseInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.*;

@RestController
@RequestMapping("/app")
public class AppclientContoller {

    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private BankCardService bankCardService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private VersionInfoService versionInfoService;
    @Autowired
    private CustomerService customerService;

    @Value("${domainUrl}")
    private String baseUrl;

    /**
     * INIT 待注册、REG_ING 注册中、REG_SUCCESS 注册成功、REG_FAIL
     *
     * @param custId
     * @return
     */
    @GetMapping("/registryStatus/{custId}")
    public InterfaceResponseInfo getRegistryStatus(@PathVariable String custId) {
        if (StringUtils.isEmpty(custId)) {
            return new InterfaceResponseInfo("0", "用户custId不能为空!", null);
        }
        MerchantPO merchant = merchantService.findMerchantByCustId(custId);
        if (merchant == null) {
            return new InterfaceResponseInfo("0", "您尚未实名认证！", "");
        }
        InterfaceResponseInfo info = null;
        Map map = new HashMap();
        map.put("desciption", merchant.getMerchantStatus());
        if ("INIT".equals(merchant.getMerchantStatus())) {
            info = new InterfaceResponseInfo("1", "待注册", map);
        } else if ("REG_ING".equals(merchant.getMerchantStatus())) {
            info = new InterfaceResponseInfo("1", "注册中", map);
        } else if ("REG_SUCCESS".equals(merchant.getMerchantStatus())) {
            info = new InterfaceResponseInfo("1", "注册成功", map);
        } else {
            info = new InterfaceResponseInfo("1", "注册失败", map);
        }
        return info;
    }

    /**
     * 银行开开卡信息 状态（INIT待开卡、OPEN_SUCCESS开卡成功、OPEN_FAIL开卡失败）
     *
     * @param cardNo
     * @param custId
     * @return
     */
    @GetMapping("/cardStatus")
    public InterfaceResponseInfo getCardStatus(String cardNo, String custId) {
        if (StringUtils.isEmpty(cardNo) || StringUtils.isEmpty(custId)) {
            return new InterfaceResponseInfo("0", "参数错误！", null);
        }
        InterfaceResponseInfo info = null;
        BankCard bankCard = bankCardService.findBankCardByCustIdAndAccountCode(custId, cardNo);
        Map map = new HashMap();
        map.put("desciption", bankCard.getOpenStatus());
        if ("INIT".equals(bankCard.getOpenStatus())) {
            info = new InterfaceResponseInfo("1", "待开卡", map);
        } else if ("OPEN_SUCCESS".equals(bankCard.getOpenStatus())) {
            info = new InterfaceResponseInfo("1", "开卡成功", map);
        } else {
            info = new InterfaceResponseInfo("1", "开卡失败", map);
        }
        return info;
    }

    @RequestMapping("/msgList")
    public InterfaceResponseInfo msgList(String custId) {
        return new InterfaceResponseInfo("1", "成功", null);
    }

    @RequestMapping("/pointList")
    public InterfaceResponseInfo pointList(String custId) {
        List<Point> list = new ArrayList<Point>();
        Point point = new Point(new Timestamp(new Date().getTime()), "签到", 1);
        list.add(point);
        point = new Point(new Timestamp(new Date().getTime()), "签到", 1);
        list.add(point);
        point = new Point(new Timestamp(new Date().getTime()), "还款", 1);
        list.add(point);
        point = new Point(new Timestamp(new Date().getTime()), "提现", 1);
        return new InterfaceResponseInfo("1", "成功", list);
    }

    @RequestMapping("/newsList")
    public InterfaceResponseInfo newsList(String custId) {
        List list = new ArrayList();
        return new InterfaceResponseInfo("1", "成功", list);
    }

    @RequestMapping("/productList")
    public InterfaceResponseInfo productList(String custId) {
        List<Product> list = new ArrayList();
        Product product = new Product("", "洗发水", 38.0, "爱生活,爱拉芳!");
        list.add(product);
        product = new Product("", "洗衣粉", 15.0, "强效去顽渍 五星真洁净!");
        list.add(product);
        return new InterfaceResponseInfo("1", "成功", list);
    }

    @GetMapping("/guide")
    public ResponseEntity<?> downloadFile(String fileName)
            throws IOException {
        return ResponseEntity.ok(resourceLoader.getResource("classpath:" + fileName + ".png"));
    }

    @GetMapping("/invitation")
    public ResponseEntity<?> invitation(String n)
            throws IOException {
        return ResponseEntity.ok(resourceLoader.getResource("classpath:invitation/" + n + ".jpg"));
    }

    @GetMapping("/invitationList")
    public InterfaceResponseInfo invitationList() throws Exception {

        List<Map<String, Object>> list = new ArrayList<>();
        DecimalFormat d = new DecimalFormat("00");

        for (int i = 2; i < 13; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("url", baseUrl + "/app/invitation.jpg?n=" + d.format(i));
            Integer height = 0;
            String type = "1";
            switch (i) {
                case 2:
                    height = 716;
                    break;
                case 3:
                    height = 684;
                    break;
                case 4:
                    height = 709;
                    break;
                case 5:
                    height = 570;
                    break;
                case 6:
                    height = 610;
                    break;
                case 7:
                    height = 521;
                    break;
                case 8:
                    height = 651;
                    break;
                case 9:
                    height = 666;
                    break;
                case 10:
                    height = 645;
                    break;
                case 11:
                    height = 685;
                    break;
                case 12:
                    height = 540;
                    break;
            }
            map.put("height", height);
            map.put("type", type);
            list.add(map);
        }
        return new InterfaceResponseInfo(list);
    }

    @RequestMapping("/download")
    public void downloadFile(org.apache.catalina.servlet4preview.http.HttpServletRequest request, HttpServletResponse response) {
        String fileName = "app-release.apk";
        File file = new File("C:\\platform\\app\\app-release.apk");
        if (file.exists()) {
            response.setContentType("application/force-download");// 设置强制下载不打开
            response.addHeader("Content-Disposition",
                    "attachment;fileName=" + fileName);// 设置文件名
            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @RequestMapping("/version")
    public InterfaceResponseInfo queryVersionByOsType() {
        VersionInfo newestOne = versionInfoService.findNewestOne();
        if (newestOne != null) {
            return new InterfaceResponseInfo("1", "成功", newestOne);
        } else {
            return new InterfaceResponseInfo("1", "失败", null);
        }
    }

    @RequestMapping("/xyz")
    public InterfaceResponseInfo xuyu() {
        Customer customer = customerService.findByCustMobile("888888");
        return new InterfaceResponseInfo("1", "SUCCESS", customer.getCustEmail());
    }

    @GetMapping("/abc/{custEmail}")
    public InterfaceResponseInfo update(@PathVariable String custEmail) {
        Customer customer = customerService.findByCustMobile("888888");
        customer.setCustEmail(custEmail);
        customerService.save(customer);
        return new InterfaceResponseInfo(null);
    }

}

class Point {
    private Timestamp obtainTime;
    private String comeFrom;
    private Integer quantity;

    public Timestamp getObtainTime() {
        return obtainTime;
    }

    public void setObtainTime(Timestamp obtainTime) {
        this.obtainTime = obtainTime;
    }

    public String getComeFrom() {
        return comeFrom;
    }

    public void setComeFrom(String comeFrom) {
        this.comeFrom = comeFrom;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Point(Timestamp obtainTime, String comeFrom, Integer quantity) {
        this.obtainTime = obtainTime;
        this.comeFrom = comeFrom;
        this.quantity = quantity;
    }

    public Point() {
    }

}

class Product {
    private String icon;
    private String name;
    private double salary;
    private String description;

    public Product(String icon, String name, double salary, String description) {
        this.icon = icon;
        this.name = name;
        this.salary = salary;
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}