package it.italiangrid.portal.hook;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Role;

import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.service.RoleLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;

public class ControlPanelChecker extends Action {

	private static Log _log = LogFactoryUtil
			.getLog(ControlPanelChecker.class);
	
	@Override
	public void run(HttpServletRequest request, HttpServletResponse response)
			throws ActionException {
		// TODO Auto-generated method stub
		_log.debug("Sono dentro all'hook");
		
		
		try {
			String reqUrl2 = request.getRequestURL().toString();
			ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(WebKeys.THEME_DISPLAY);
			String reqUrl = PortalUtil.getLayoutURL(themeDisplay.getLayout(),themeDisplay);
			
			_log.debug("pronto per il check di: " + reqUrl2);
			if(reqUrl2.contains("/c/search")){
				_log.debug("trovato search");
				response.sendRedirect(request.getContextPath()+ "/web/guest/welcome");
				return;
			}
			
			if(request.getRemoteUser()==null)
				return;
			
			long userId = Long.parseLong(request.getRemoteUser());
			
			_log.debug("Recuperato userId: "+ userId);
			
			
			//LastPath lastPath = new LastPath(request.getContextPath(), reqUrl.replace(request.getContextPath(), ""));// ;
			long companyId = PortalUtil.getCompanyId(request);
			_log.debug("Recuperato companyId: "+ companyId);
			Role adminRole;
			boolean isAdmin = false;
			
			
			adminRole = RoleLocalServiceUtil.getRole(companyId, "Administrator");
			long[] users = UserLocalServiceUtil.getRoleUserIds(adminRole.getRoleId());
			for (long l : users) {
				_log.debug("users: "+ l);
				if(l == userId){
					isAdmin = true;
					break;
				}
				_log.debug("isAdmin: "+ isAdmin);
			}
		
			
			if(!isAdmin){
				_log.debug("pronto per il check di: " + reqUrl);
				if(reqUrl.contains("/group/control_panel")){
					_log.debug("trovato control panel");
					response.sendRedirect(request.getContextPath()+ "/web/guest/welcome");
				}
			}
		
		} catch (PortalException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SystemException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		_log.debug("Esco dall'hook");
	}

}
