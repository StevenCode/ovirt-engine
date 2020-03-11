package org.ovirt.engine.core.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.ovirt.engine.core.utils.ovf.xml.XmlDocument;
import org.ovirt.engine.core.utils.ovf.xml.XmlNode;
import org.ovirt.engine.core.utils.ovf.xml.XmlNodeList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * VgpuTypeUtils.
 *
 * @author shidingfeng
 */
public class VgpuTypeUtils {
    private static final Logger log = LoggerFactory.getLogger(VgpuTypeUtils.class);
    private static final String VGPU_XML_DATA = "vgpuConfig.xml";

    private static XmlDocument xmlDocument;
    private static final HashMap<String, String> vgpuTypeMap = new HashMap<>();
    private static final HashMap<String, String> vgpuNameMap = new HashMap<>();

    private static String getxmlOfVGPU() throws IOException {
        InputStream resourceAsStream = VgpuTypeUtils.class.getClassLoader().getResourceAsStream(VGPU_XML_DATA);
        BufferedReader buffReader =new BufferedReader(new InputStreamReader(resourceAsStream));
        StringBuilder sb = new StringBuilder();
        String strTmp = "";
        while((strTmp = buffReader.readLine())!=null){
            sb.append(strTmp);
        }
        buffReader.close();
        return sb.toString();
    }

    static {
        xmlDocument = null;
        try {
            xmlDocument = new XmlDocument(getxmlOfVGPU());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("load vgpuConfig.xml error", e);
        }
        XmlNode vgpuconfig = xmlDocument.selectSingleNode("vgpuconfig");
        XmlNodeList vgpuTypes = vgpuconfig.selectNodes("vgpuType");
        for (XmlNode vgpuType : vgpuTypes) {
            String id = vgpuType.attributes.get("id").getValue();
            id = "nvidia-" + id;
            String name = vgpuType.attributes.get("name").getValue();
            String numHeads = vgpuType.selectSingleNode("numHeads").innerText;
            XmlNode display = vgpuType.selectSingleNode("display");
            String width = display.attributes.get("width").getValue();
            String height = display.attributes.get("height").getValue();
            StringBuilder sb = new StringBuilder();
            sb.append(name);
            sb.append(" virtual GPU ");
            sb.append("(");
            sb.append(width);
            sb.append("x");
            sb.append(height);
            sb.append(", ");
            sb.append(numHeads);
            sb.append(" display");
            sb.append(")");
            String showName = sb.toString();
            if (id != null && name != null) {
                vgpuTypeMap.put(id, showName);
                vgpuNameMap.put(showName, id);
            }
        }
    }

    public static String getVgpuNamebyType(String type) {
        if (!vgpuTypeMap.containsKey(type)) {
            return type;
        }
        return vgpuTypeMap.get(type);
    }

    public static HashMap<String, String> getVgpuNameMap() {
        return vgpuNameMap;
    }


}
