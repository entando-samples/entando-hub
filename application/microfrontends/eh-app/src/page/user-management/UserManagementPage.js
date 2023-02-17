import React, { useEffect, useState } from "react"

import {
  Content,
  DataTable,
  DataTableSkeleton,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableHeader,
  TableRow,
  TableToolbar,
  TableToolbarContent,
} from "carbon-components-react"
import UserManagementOverflowMenu from "./overflow-menu/UserManagementOverflowMenu"
import { ModalAddNewUser } from "./modal-add-new-user/ModalAddNewUser"
import {
  getAllUsers,
  getSingleOrganisation,
} from "../../integration/Integration"
import EhBreadcrumb from "../../components/eh-breadcrumb/EhBreadcrumb"
import "./user-managment-page.scss"
import i18n from "../../i18n"
import { SHOW_NAVBAR_ON_MOUNTED_PAGE } from "../../helpers/constants"
import { useApiUrl } from "../../contexts/ConfigContext"

/*
BUNDLEGROUP:
{
name	string
description	string
descriptionImage	string
documentationUrl	string
status	string
Enum:
Array [ 2 ]
children	[...]
organisationId	string
categories	[...]
bundleGroupId	string
}


BUNDLE
{
name	string
description	string
gitRepoAddress	string
dependencies	[...]
bundleGroups	[...]
bundleId	string
}
 */

const headers = [
  {
    key: "username",
    header: "username",
  },
  {
    key: "email",
    header: "email",
  },
  {
    key: "organisation",
    header: "organisation",
  },
  {
    key: "overflow",
    header: "",
  },
]

const UserManagementPage = () => {

  const [reloadToken, setReloadToken] = useState(
    new Date().getTime().toString()
  )
  const [users, setUsers] = useState([])
  const [loading, setLoading] = useState(true)

  const apiUrl = useApiUrl();

  // fetches the users to show
  useEffect(() => {
    //TODO BE QUERY REFACTORING
    const getAllPortalUsers = async () => {
      setLoading(true)
      const userList = (await getAllUsers(apiUrl)).userList

      if (userList === undefined) {
        setLoading(false)
      }
      //for every user get the organisations name
      const userListWithOrganisation = await Promise.all(
        userList && userList.map(async (user) => {

            if (user.organisationIds) {
            //get the current organisation name
            const organisations = await Promise.all(
              user.organisationIds.map(async (oid) => {
                const organisation = (await getSingleOrganisation(apiUrl, oid))
                  .organisation
                return organisation
              })
            )
            return {
              ...user,
              email: user.email ? user.email : "",
              organisation: organisations[0],
            }
          }
            return {
            ...user,
            email: user.email ? user.email : "",
            organisation: null,
          }
        })
      )

      if (userListWithOrganisation === undefined) {
        setLoading(false)
      }

      return userListWithOrganisation
    }
    ;(async () => {
      setUsers(await getAllPortalUsers(apiUrl))
      setLoading(false)
    })()
  }, [apiUrl, reloadToken])

  const onAfterSubmit = () => {
    setReloadToken(new Date().getTime().toString())
  }
  return (
    <>
      <Content className="UserManagementPage">
        <div className="UserManagementPage-wrapper">
          <div className="bx--row">
            <div className="bx--col-lg-16 UserManagementPage-breadcrumb">
              <EhBreadcrumb
                pathElements={[{
                  path: i18n.t('navLink.userManagement'),
                  page: SHOW_NAVBAR_ON_MOUNTED_PAGE.isUserManagementPage
                }]}
              />
            </div>
          </div>
          <div className="bx--row">
            <div className="bx--col-lg-16 UserManagementPage-section">
              {loading && <DataTableSkeleton columnCount={4} rowCount={4} />}

              {!loading && (
                <DataTable rows={users} headers={headers}>
                  {({
                    rows,
                    headers,
                    getTableProps,
                    getHeaderProps,
                    getRowProps,
                  }) => (
                    <TableContainer title={i18n.t("page.admin.userManagement")}>
                      <TableToolbar>
                        <TableToolbarContent>
                          <ModalAddNewUser onAfterSubmit={onAfterSubmit} />
                        </TableToolbarContent>
                      </TableToolbar>
                      <Table {...getTableProps()}>
                        <TableHead>
                          <TableRow>
                            {headers.map((header) => (
                              <TableHeader {...getHeaderProps({ header })}>
                                {header.header ? i18n.t(`page.admin.${header.header}`) : ''}
                              </TableHeader>
                            ))}
                          </TableRow>
                        </TableHead>
                        <TableBody>
                          {rows.map((row) => (
                            <TableRow {...getRowProps({ row })}>
                              {row.cells.map((cell, index) => {
                                if (cell.id !== row.id + ":overflow")
                                  return (
                                    <TableCell key={cell.id}>
                                      {index === 2
                                        ? cell.value
                                          ? cell.value.name
                                          : "---"
                                        : cell.value}
                                    </TableCell>
                                  )
                                return (
                                  <TableCell key={cell.id}>
                                    <UserManagementOverflowMenu
                                      userObj={{
                                        username: row.cells[0].value,
                                        email: row.cells[1].value,
                                        organisation: row.cells[2].value,
                                      }}
                                      onAfterSubmit={onAfterSubmit}
                                    />
                                  </TableCell>
                                )
                              })}
                            </TableRow>
                          ))}
                        </TableBody>
                      </Table>
                    </TableContainer>
                  )}
                </DataTable>
              )}
            </div>
          </div>
        </div>
      </Content>
    </>
  )
}

export default UserManagementPage
