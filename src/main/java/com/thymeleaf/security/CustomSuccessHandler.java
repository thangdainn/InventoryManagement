package com.thymeleaf.security;

import com.thymeleaf.dto.*;
import com.thymeleaf.jwt.JwtTokenProvider;
import com.thymeleaf.payload.response.JwtResponse;
import com.thymeleaf.repository.IAuthRepository;
import com.thymeleaf.repository.IRoleRepository;
import com.thymeleaf.service.*;
import com.thymeleaf.utils.Constant;
import com.thymeleaf.utils.SecurityUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Autowired
    private IRoleService roleService;

    @Autowired
    private IAuthService authService;

    @Autowired
    private IMenuService menuService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private ITokenService tokenService;

    @Autowired
    private IUserService userService;

//    @Autowired
//    private IAuthRepository authRepository;

//    @Autowired
//    private IRoleRepository roleRepository;

    @Override
    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, authentication);
        if (response.isCommitted()) {
            return;
        }
        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, Authentication authentication) {
        String url;
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<MenuDTO> menuList = new ArrayList<>();
        List<String> roles = SecurityUtil.getAuthorities();

        RoleDTO role = new RoleDTO();
        if (isAdmin(roles)) {
            role.setName("ROLE_ADMIN");
        } else if (isStaff(roles)) {
            role.setName("ROLE_STAFF");
        } else if (isEmployee(roles)) {
            role.setName("ROLE_EMPLOYEE");
        }
        role = roleService.findByName(role);
        List<AuthDTO> auths = authService.findByRole_Id(role.getId());
        List<MenuDTO> menuChildList = new ArrayList<>();
        for (AuthDTO auth : auths) {
            MenuDTO menu = new MenuDTO();
            menu.setId(auth.getMenuId());
            menu = menuService.findById(menu.getId());
            if (menu.getParentId() == 0 && menu.getOrderIndex() != -1 && menu.getActiveFlag() == 1 &&
                    auth.getPermission() == 1) {
                menu.setIdMenu(menu.getUrl().replace("/", "") + "Id");
                menuList.add(menu);
            } else if (menu.getParentId() != 0 && menu.getOrderIndex() != -1 && menu.getActiveFlag() == 1 &&
                    auth.getPermission() == 1) {
                menu.setIdMenu(menu.getUrl().replace("/", "") + "Id");
                menuChildList.add(menu);
            }
        }
        for (MenuDTO menu : menuList) {
            List<MenuDTO> childList = new ArrayList<>();
            for (MenuDTO childMenu : menuChildList) {
                if (childMenu.getParentId().equals(menu.getId())) {
                    childList.add(childMenu);
                }
            }
            menu.setChild(childList);
        }
        sortMenu(menuList);
        for (MenuDTO menu : menuList) {
            sortMenu(menu.getChild());
        }
        url = "/home";
        CustomUserDetail customUserDetail = (CustomUserDetail) authentication.getPrincipal();
        UserDTO userDTO = userService.findByUserName(customUserDetail.getUsername(), 1);
        TokenDTO tokenDTO = tokenProvider.generateToken(customUserDetail.getUsername(), userDTO.getProviderId());
        tokenDTO.setUserId(userDTO.getId());
        tokenService.save(tokenDTO);
        List<String> listRole = customUserDetail.getAuthorities().stream()
                .map(item -> item.getAuthority()).collect(Collectors.toList());
        JwtResponse jwtResponse = new JwtResponse(tokenDTO.getToken(), tokenDTO.getRefreshToken(), customUserDetail.getUsername(), listRole);
        HttpSession session = request.getSession();
        session.setAttribute(Constant.JWT, jwtResponse);
        session.setAttribute(Constant.MENUS, menuList);
        session.setAttribute(Constant.USER, userDetails);
        return url;
    }

    private boolean isAdmin(List<String> roles) {
        return roles.contains("ROLE_ADMIN");
    }

    private boolean isStaff(List<String> roles) {
        return roles.contains("ROLE_STAFF");
    }

    private boolean isEmployee(List<String> roles) {
        return roles.contains("ROLE_EMPLOYEE");
    }

    private void sortMenu(List<MenuDTO> menus) {
        menus.sort(new Comparator<MenuDTO>() {
            @Override
            public int compare(MenuDTO o1, MenuDTO o2) {
                return o1.getOrderIndex() - o2.getOrderIndex();
            }
        });
    }
}
