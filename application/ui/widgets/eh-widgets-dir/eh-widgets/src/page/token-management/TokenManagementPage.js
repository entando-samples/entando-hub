import React, {useEffect, useState} from "react"

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
  OverflowMenu, 
  OverflowMenuItem
} from "carbon-components-react"
// import CategoryManagementOverflowMenu from "../catagory-management/overflow-menu/CategoryManagementOverflowMenu"
// import {ModalAddNewCategory} from "../catagory-management/modal-add-new-category/ModalAddNewCategory"
import {ModalAddNewToken} from "./modal-add-new-token/ModalAddNewToken"
import {getAllTokens} from "../../integration/Integration"
import EhBreadcrumb from "../../components/eh-breadcrumb/EhBreadcrumb"
import "./token-management-page.scss"
import i18n from "../../i18n"
import { SHOW_NAVBAR_ON_MOUNTED_PAGE } from "../../helpers/constants"
import { Edit24, TrashCan24 } from '@carbon/icons-react'

const headers = [
  {
    key: "name",
    header: "Name",
  },
{
    key: "overflow",
    header:""
}
]

const TokenManagementPage = () => {
  const [reloadToken, setReloadToken] = useState(
    new Date().getTime().toString()
  )
  const [tokens, setTokens] = useState([])
  const [isLoading, setIsLoading] = useState(true);

  // fetches the users to show
  useEffect(() => {
    (async () => {
      setIsLoading(true)
      const tokenList = (await getAllTokens()).tokenList;
      if (tokenList === undefined) {
        setIsLoading(false)
      }
      setTokens(tokenList.map(token=>{
        return {
          id: token.tokenId,
          ...token
        }
      }))
      setIsLoading(false)
    })()
  }, [reloadToken])

  const onEditClick = () => {
    console.log("I am clicking EDIT")
  }

  const onDeleteClick = () => {
    console.log("I am clicking Delete")
  }

  const sampleTokenData = [
    {
        "id": "1",
        "name": "Token 1",
        "tokenData": "sdf2PgQTJ4mu",
    },
    {
        "id": "2",
        "name": "Token 2",
        "tokenData": "aaPg23sg2QTJ4mu",
    },
    {
        "id": "3",
        "name": "Token 3",
        "tokenData": "aaPgQsdf2J4mu",

    }
];

  return (
    <>
      <Content className="TokenManagmentPage">
        <div className="TokenManagmentPage-wrapper">
          <div className="bx--row">
            <div className="bx--col-lg-16 TokenManagmentPage-breadcrumb">
              <EhBreadcrumb
                pathElements={[{
                  path: i18n.t('navLink.tokenManagement'),
                  page: SHOW_NAVBAR_ON_MOUNTED_PAGE.isTokenManagmentPage
                }]}
              />
            </div>
          </div>
          <div className="bx--row">
            <div className="bx--col-lg-16 TokenManagmentPage-section">
              {isLoading && <DataTableSkeleton columnCount={3} rowCount={10}/>}
              {!isLoading && (
              <DataTable 
              rows={sampleTokenData} 
              headers={headers}>
                {({
                  rows,
                  headers,
                  getTableProps,
                  getHeaderProps,
                  getRowProps,
                }) => (
                  <TableContainer title={i18n.t('navLink.tokenManagement')}>
                    <TableToolbar>
                      <TableToolbarContent>
                        <ModalAddNewToken />
                      </TableToolbarContent>
                    </TableToolbar>
                    <Table {...getTableProps()}>
                      <TableHead>
                        <TableRow>
                          {headers.map((header) => (
                            <TableHeader {...getHeaderProps({ header })}>
                              {header.header ? i18n.t(`component.bundleModalFields.${header.header.toLowerCase()}`) : ''}
                            </TableHeader>
                          ))}
                        </TableRow>
                      </TableHead>
                      <TableBody>
                        {rows.map((row) => (
                          <TableRow {...getRowProps({ row })}>
                            {row.cells.map((cell, index) => {
                                if ((cell.id !== row.id + ":overflow") && cell.id !== row.id + ":tokenData") {
                                    return (
                                    <TableCell key={cell.id}>
                                        {cell.value} 
                                    </TableCell>
                                    )
                                }
                                return (
                                    <TableCell key={cell.id} align={"right"}>
                                        <OverflowMenu>
                                            <OverflowMenuItem 
                                            itemText={<Edit24 />}
                                            onClick={() => onEditClick()} 
                                            />
                                            <OverflowMenuItem 
                                            itemText={<TrashCan24 />}
                                            onClick={() => onDeleteClick()} 
                                            />
                                        </OverflowMenu>
                                    </TableCell>
                                )
                            })}
                          </TableRow>
                        ))}
                      </TableBody>
                    </Table>
                  </TableContainer>
                )}
              </DataTable>)}
            </div>
          </div>
        </div>
      </Content>
    </>
  )
}

export default TokenManagementPage
