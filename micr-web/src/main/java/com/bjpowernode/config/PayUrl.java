package com.bjpowernode.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "pay")
public class PayUrl {
    private String kqUrl;
    private String baofuUrl;

    public String getKqUrl() {
        return kqUrl;
    }

    public void setKqUrl(String kqUrl) {
        this.kqUrl = kqUrl;
    }

    public String getBaofuUrl() {
        return baofuUrl;
    }

    public void setBaofuUrl(String baofuUrl) {
        this.baofuUrl = baofuUrl;
    }

    @Override
    public String toString() {
        return "PayUrl{" +
                "kqUrl='" + kqUrl + '\'' +
                ", baofuUrl='" + baofuUrl + '\'' +
                '}';
    }
}
