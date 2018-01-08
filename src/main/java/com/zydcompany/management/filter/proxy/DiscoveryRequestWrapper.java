package com.zydcompany.management.filter.proxy;

import org.apache.commons.io.IOUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 为支持多次读取http的body流，需要通过对HttpServletRequest做包装类
 */
public class DiscoveryRequestWrapper extends
        HttpServletRequestWrapper {

    private byte[] body;

    public DiscoveryRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        init();
    }

    private void init() throws IOException {
        body = IOUtils.toByteArray(super.getInputStream());
    }

    @Override
    public BufferedReader getReader() throws IOException {
        ServletInputStream inputStream = this.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, super.getCharacterEncoding());
        return new BufferedReader(inputStreamReader);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream bais = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            private int offset;

            @Override
            public boolean isFinished() {
                return offset >= body.length;
            }

            @Override
            public int read() throws IOException {
                offset++;
                return bais.read();
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }
        };
    }
}