package com.goldgyro.platform.foreground.constants;

public class AlipayConfig {
    // 1.商户appid 2088622889197893
    public static String APPID = "2018080860944436";

    // 2.私钥 pkcs8格式的
    public static String RSA_PRIVATE_KEY ="MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCkPRNHwYfMXN59P5qd2bBHqe5WK+evnuoNxvUxWHcXizysrqwInIcp8LpDS/+YH56fSBxN4uPtYJvimEgPbrQDGHzAPYzfwUTj3qUfzs4kfcRgpgh+G8Gh9ZBzMh7kD0XvRNJN5aCLB42TQfFdinPz9CTPKP3DwwOCugLv4oBjROlqRS16ocDSSWYrM0KyszOorKyxfbGHKEhVXtwTv5D1FrBVhnfFohmMewtWlKxqgY9VpNneJj7/DhFsgG0NBPLX/VHRL1f/uty/KrJg7MXobHUxaBYfGRmqB+Yx5/lQPG5SAUjgBShyOALeumDcGHrUhlM3VnDnOvoJj4mlU5IbAgMBAAECggEBAIGtf6WulyyzPzEhoVF1aoyLDhaYAA1YqnFKYJsfwJikZbnrCQaboNVqr4cnYJ6aiMI3S7Hrrg/lse/OUUduH1A6a3XWo36guWY6i6J7xaRmcUHmltoLzG222OE798wWix+K9Ypv/r0Hr69P5+X1xewfH1k6gnOpbQIxINSLwNefc4FjmrIqHFnB6sZQQ87ggABTS4kj8lX17/QP54BQ3pUiEgaYtGrzbjEMi9ZH9Du7AhohZrRShLcDW8ZutnI9kv07aaGMYdWqj2memL8B4gLV4yhjv4iekvAikYraAhuDykwxCPKmtc5KnWTAptDiSLZNE9sUiuvgQ1LozPlM8nECgYEA13bhCUwgEAg3dWUGKwjMfuXqpwSRpZb65kMmLA6abchXc8cLVRmP6FZHOgJ2XimDSTM7PdsCAei6HUZjHlCttyRo7VtLyYAqOq27dL7eT0W3XBFHJ8vLgyaMP+fNFvVTaMf9go5WEaPcaq8ttNYFZ2B68g6aOa1WqsVwPWk0wKUCgYEAwyMTvQNVPfRgqI2aruKlAJuZWLzOJeljaEt4vJrTthQEePB2ZBYXWfwJ+rugKImkbtCeBrnElWDGEBoSCxUYHDaXLauEVc17afmylzg7XAuooZGu4zPCSSKeUknFVOz7p2SoDpwXl6Wo4cEd714t8QcJt2sgD5tHAtXWdIZRy78CgYAEWKeNcnWrHR9RROps786AOM8U82ikuHjzMCOS+/Btin8JHNjDrLJUc3kYWHheyeOWFGx7gPF3bGRktsjFEcePTDK5pv/OXsF7UYnYfwko/DUIrGiNCvVd/ecSWvCuJ+C42l/oxEpELNQDauk3HqIZRb71JwpBY5e99iGfe4HO6QKBgQCzX8QeJDkwSTqs1fYRuq84Xs/YXT4WZXKGhZHHtX7VSZNO5KO/nvr6MY+u1tgDS3tGjiglQJBvS9TlKkHZxBs0nA3d4mM0SD2hDfHXxc3y75ikZHlhystWVSc5tyxYw7sUhZdiTqP1N0eFtfJhfa43eGO7IhMHpZHiDlUjKfF4mwKBgAoq5uChaeAeLYudwsw98wdfbBFDQI3PywDe++Na1q0d0HpAbVvh9Dv3Z+Yv5vDT58B67SYjBSia9o0j1yrH1in0tohNoVDhvKtImp7md+fRKd6Mm0XQiIuesnN6dfdptH+fmNn1GSKGjR+dE2hk5mnEn5/txld+DStpueto6GYv";

    // 3.支付宝公钥
    public static String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApD0TR8GHzFzefT+andmwR6nuVivnr57qDcb1MVh3F4s8rK6sCJyHKfC6Q0v/mB+en0gcTeLj7WCb4phID260Axh8wD2M38FE496lH87OJH3EYKYIfhvBofWQczIe5A9F70TSTeWgiweNk0HxXYpz8/Qkzyj9w8MDgroC7+KAY0TpakUteqHA0klmKzNCsrMzqKyssX2xhyhIVV7cE7+Q9RawVYZ3xaIZjHsLVpSsaoGPVaTZ3iY+/w4RbIBtDQTy1/1R0S9X/7rcvyqyYOzF6Gx1MWgWHxkZqgfmMef5UDxuUgFI4AUocjgC3rpg3Bh61IZTN1Zw5zr6CY+JpVOSGwIDAQAB";

    // 4.服务器异步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://47.106.103.104/alipay/notify_url";

    // 5.页面跳转同步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 商户可以自定义同步跳转地址
    public static String return_url = "http://47.106.103.104/return_url";

    // 6.请求网关地址
    public static String URL = "https://openapi.alipay.com/gateway.do";

    // 7.编码
    public static String CHARSET = "UTF-8";

    // 8.返回格式
    public static String FORMAT = "json";

    // 9.加密类型
    public static String SIGNTYPE = "RSA2";

}