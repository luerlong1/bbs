package cms.web.action.upgrade.impl;

import java.io.File;
import java.util.Date;
import cms.bean.template.Templates;
import cms.bean.upgrade.UpgradeLog;
import cms.bean.upgrade.UpgradeSystem;
import cms.service.template.TemplateService;
import cms.service.upgrade.UpgradeService;
import cms.utils.FileUtil;
import cms.utils.JsonUtils;
import cms.utils.SpringConfigTool;
import cms.web.action.fileSystem.localImpl.LocalFileManage;
import cms.web.action.upgrade.UpgradeManage;
/**
 * 4.3升级到4.4版本执行程序
 *
 */
public class Upgrade4_3to4_4 {
	
	
	/**
	 * 运行
	 * @param upgradeId 升级Id
	 */
    public static void run(String upgradeId){
    	UpgradeService upgradeService = (UpgradeService)SpringConfigTool.getContext().getBean("upgradeServiceBean");
    	UpgradeManage upgradeManage = (UpgradeManage)SpringConfigTool.getContext().getBean("upgradeManage");
    	LocalFileManage localFileManage = (LocalFileManage)SpringConfigTool.getContext().getBean("localFileManage");
    	TemplateService templateService = (TemplateService)SpringConfigTool.getContext().getBean("templateServiceBean");
    	for(int i =0; i< 100; i++){
    		upgradeManage.taskRunMark_delete();
			upgradeManage.taskRunMark_add(1L);
    		
    		UpgradeSystem upgradeSystem = upgradeService.findUpgradeSystemById(upgradeId);
    		if(upgradeSystem == null || upgradeSystem.getRunningStatus().equals(9999)){
    			break;
    		}
    		if(upgradeSystem.getRunningStatus()>=100 && upgradeSystem.getRunningStatus()<200){
    			
    			
    			//插入升级SQL
    			Templates templates =templateService.findTemplatebyDirName("default");//查询模板
    			if(templates != null){
    				
    				insertSQL_forum(upgradeService);
    				upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表forum插入SQL成功",1))+",");
    				
    				insertSQL_layout(upgradeService);
    				upgradeService.addLog(upgradeId, JsonUtils.toJSONString(new UpgradeLog(new Date(),"表layout插入SQL成功",1))+",");
    			}
    			
    			
    			//更改运行状态
				upgradeService.updateRunningStatus(upgradeId ,200,JsonUtils.toJSONString(new UpgradeLog(new Date(),"升级流程结束",1))+",");

    		}
    		
    		
    		if(upgradeSystem.getRunningStatus()>=200 && upgradeSystem.getRunningStatus()<9999){
    			//更改运行状态
				upgradeService.updateRunningStatus(upgradeId ,9999,JsonUtils.toJSONString(new UpgradeLog(new Date(),"升级完成",1))+",");
				//写入当前BBS版本
				FileUtil.writeStringToFile("WEB-INF"+File.separator+"data"+File.separator+"systemVersion.txt",upgradeSystem.getId(),"utf-8",false);
				
				//临时目录路径
				String temp_path = "WEB-INF"+File.separator+"data"+File.separator+"temp"+File.separator+"upgrade"+File.separator;
				//删除临时文件夹
				localFileManage.removeDirectory(temp_path+upgradeSystem.getUpdatePackageFirstDirectory()+File.separator);
				
    		}
    		
    		
    		
    		
    	}
    	upgradeManage.taskRunMark_delete();
    }
    

    /**
   	 * 插入升级SQL
   	 * @param upgradeService
   	 */
    private static void insertSQL_forum(UpgradeService upgradeService){
       	
       	String sql = "INSERT INTO `forum` (`id`,`dirName`,`displayType`,`formValue`,`forumChildType`,`forumType`,`invokeMethod`,`layoutFile`,`layoutId`,`layoutType`,`module`,`name`,`queryMode`,`referenceCode`) VALUES (NULL,'default','collection',NULL,'修改评论','话题',1,'blank_13.html','dc8425d53c864352b24969d889177321',4,'topicRelated_editComment_collection_default','修改评论',0,'topicRelated_editComment_1'),(NULL,'default','collection',NULL,'修改评论回复','话题',1,'blank_14.html','20f3f5229e244aaa9640fa4458f2451e',4,'topicRelated_editReply_collection_default','修改评论回复',0,'topicRelated_editReply_1'),(NULL,'default','collection',NULL,'修改评论','话题',1,NULL,'e3f72ab4c65745a8a2a4bdd8ffa65a04',4,'topicRelated_editComment_collection_default','修改评论',0,'topicRelated_editComment_2'),(NULL,'default','collection',NULL,'修改评论回复','话题',1,NULL,'b35b9b31d4584b65a1109203b6119476',4,'topicRelated_editReply_collection_default','修改评论回复',0,'topicRelated_editReply_2');";
       	upgradeService.insertNativeSQL(sql);
    }
   	
   /**
  	 * 插入升级SQL
  	 * @param upgradeService
  	 */
    private static void insertSQL_layout(UpgradeService upgradeService){
    	String sql = "INSERT INTO `layout` (`id`,`dirName`,`forumData`,`layoutFile`,`name`,`referenceCode`,`returnData`,`sort`,`type`) VALUES ('20f3f5229e244aaa9640fa4458f2451e','default',-1,'blank_14.html','修改评论回复','user/editCommentReply',0,2140,4),('b35b9b31d4584b65a1109203b6119476','default',-1,NULL,'修改评论回复(移动版)','queryEditCommentReply',1,2160,4),('dc8425d53c864352b24969d889177321','default',-1,'blank_13.html','修改评论','user/editComment',0,2130,4),('e3f72ab4c65745a8a2a4bdd8ffa65a04','default',-1,NULL,'修改评论(移动版)','queryEditComment',1,2150,4);";
        upgradeService.insertNativeSQL(sql);
        
        
    }
    
   

}
