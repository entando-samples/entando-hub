import React, {useEffect, useState} from "react"


import {
    Content,
    DataTable,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableHeader,
    TableRow,
    TableToolbar,
    TableToolbarContent,
} from 'carbon-components-react'
import UserManagementOverflowMenu from "./overflow-menu/UserManagementOverflowMenu"
import {ModalAddNewUser} from "./modal-add-new-user/ModalAddNewUser"
import {getAllUsers, getSingleOrganisation} from "../../integration/Integration"
import CatalogPageHeader from "../catalog/catalog-page-header/CatalogPageHeader";
import EhBreadcrumb from "../../components/eh-bradcrumb/EhBreadcrumb";
import CatalogPageFooter from "../catalog/catalog-page-footer/CatalogPageFooter";

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
        key: 'username',
        header: 'Username',
    },
    {
        key: 'email',
        header: 'Email',
    },
    {
        key: 'organisation',
        header: 'Organisation',
    },
    {
        key: 'overflow',
        header: 'overflow',
    }
]

/*
{
    "id": "string",
    "created": "2021-09-22T13:37:39.364Z",
    "username": "string",
    "enabled": true,
    "firstName": "string",
    "lastName": "string",
    "email": "string",
    "organisationIds": [
      "string"
    ]
  }
*/


const UserManagementPage = () => {
    const [reloadToken, setReloadToken] = useState(((new Date()).getTime()).toString())
    const [users, setUsers] = useState([])

    // fetches the users to show
    useEffect(() => {
        const init = async () => {
            //users already inserted in the portalUsers
            const userList = (await getAllUsers()).userList

            //for every user get the organisations name
            const userListWithOrganisation = await Promise.all(userList.map((async (user) => {
                if (user.organisationIds) {
                    //get the current organisation name
                    const organisations = await Promise.all(user.organisationIds.map((async (oid) => {
                            const organisation = (await getSingleOrganisation(oid)).organisation
                            return organisation
                        }
                    )))

                    return {
                        ...user,
                        organisation: organisations[0]
                    }
                }

                return {
                    ...user,
                    organisation: null
                }
            })))
            setUsers(userListWithOrganisation)
        }

        init()
    }, [reloadToken])

    const onAfterSubmit = () => {
        setReloadToken(((new Date()).getTime()).toString())
    }


    return (
        <>
            <CatalogPageHeader/>
            <Content className="CatalogPage">
                <div className="bx--grid bx--grid--full-width catalog-page">
                    <div className="bx--row">
                        <div className="bx--col-lg-16 CatalogPage-breadcrumb">
                            <EhBreadcrumb/>
                        </div>
                    </div>
                    <div className="bx--row">
                        <div className="bx--col-lg-16 CatalogPage-section">
                            <DataTable rows={users} headers={headers}>
                                {({rows, headers, getTableProps, getHeaderProps, getRowProps}) => (
                                    <TableContainer title="Users Management">
                                        <TableToolbar>
                                            <TableToolbarContent>
                                                <ModalAddNewUser onAfterSubmit={onAfterSubmit}/>
                                            </TableToolbarContent>
                                        </TableToolbar>
                                        <Table {...getTableProps()}>
                                            <TableHead>
                                                <TableRow>
                                                    {headers.map((header) => (
                                                        <TableHeader {...getHeaderProps({header})}>
                                                            {header.header}
                                                        </TableHeader>
                                                    ))}
                                                </TableRow>
                                            </TableHead>
                                            <TableBody>
                                                {rows.map(row => (
                                                    <TableRow {...getRowProps({row})}>
                                                        {row.cells.map((cell, index) => {
                                                                if (cell.id !== row.id + ":overflow") return <TableCell
                                                                    key={cell.id}>{index === 2 ? cell.value ? cell.value.name : "---" : cell.value}</TableCell>
                                                                return <TableCell key={cell.id}><UserManagementOverflowMenu
                                                                    userObj={{
                                                                        username: row.cells[0].value,
                                                                        email: row.cells[1].value,
                                                                        organisation: row.cells[2].value
                                                                    }} onAfterSubmit={onAfterSubmit}/></TableCell>
                                                            }
                                                        )}
                                                    </TableRow>
                                                ))}
                                            </TableBody>
                                        </Table>
                                    </TableContainer>
                                )}
                            </DataTable>
                        </div>
                    </div>
                </div>
            </Content>
            <CatalogPageFooter/>
        </>


    )
}


export default UserManagementPage
